package com.example.demo.Services;

import com.example.demo.DTOs.*;
import com.example.demo.Entities.*;
import com.example.demo.Entities.ReservationStatus;
import com.example.demo.Entities.RoomStatus;
import com.example.demo.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private ReservationRoomRepository reservationRoomRepository;

    @Autowired
    private StayRepository stayRepository;

    // ========== OPTIMIZED METHODS WITH JOIN FETCH ==========

    // Cache all reservations (fast after first load) - USING OPTIMIZED METHOD
    @Cacheable(value = "reservations", key = "'all'")
    public List<ReservationDTO> getAllReservations() {
        System.out.println("⚠️ CACHE MISS: Fetching ALL reservations from database (optimized)...");
        List<Reservation> reservations = reservationRepository.findAllWithDetails();
        System.out.println("✅ Fetched " + reservations.size() + " reservations with JOIN FETCH");
        return reservations.stream()
                .map(this::convertToDTOOptimized)
                .collect(Collectors.toList());
    }

    // Cache reservations by guest - USING OPTIMIZED METHOD
    @Cacheable(value = "reservations", key = "'guest_' + #guestId")
    public List<ReservationDTO> getReservationsByGuest(Long guestId) {
        System.out.println("⚠️ CACHE MISS: Fetching reservations for guest " + guestId + " (optimized)...");
        List<Reservation> reservations = reservationRepository.findByGuestIdWithDetails(guestId);
        System.out.println("✅ Fetched " + reservations.size() + " reservations for guest " + guestId + " with JOIN FETCH");
        return reservations.stream()
                .map(this::convertToDTOOptimized)
                .collect(Collectors.toList());
    }

    // Cache individual reservation - USING OPTIMIZED METHOD
    @Cacheable(value = "reservations", key = "'reservation_' + #reservationId")
    public ReservationDTO getReservationById(Long reservationId) {
        System.out.println("⚠️ CACHE MISS: Fetching reservation " + reservationId + " (optimized)...");
        Reservation reservation = reservationRepository.findByIdWithDetails(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        System.out.println("✅ Fetched reservation " + reservationId + " with JOIN FETCH");
        return convertToDTOOptimized(reservation);
    }

    // ========== CREATE, UPDATE, DELETE OPERATIONS ==========

    // Clear ALL reservation caches when creating new reservation
    @Caching(evict = {
            @CacheEvict(value = "reservations", key = "'all'"),
            @CacheEvict(value = "reservations", key = "'guest_' + #createReservationDTO.guestId"),
            @CacheEvict(value = "rooms", allEntries = true)
    })
    @Transactional
    public ReservationDTO createReservation(CreateReservationDTO createReservationDTO) {
        // Validate dates
        if (createReservationDTO.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        if (createReservationDTO.getCheckOutDate().isBefore(createReservationDTO.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // Get guest
        Guest guest = guestRepository.findById(createReservationDTO.getGuestId())
                .orElseThrow(() -> new RuntimeException("Guest not found"));

        // Check room availability
        List<Room> rooms = new ArrayList<>();
        for (Long roomId : createReservationDTO.getRoomIds()) {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));

            if (room.getStatus() != RoomStatus.AVAILABLE) {
                throw new RuntimeException("Room " + room.getRoomNumber() + " is not available");
            }

            // Check if room is already booked for the dates
            boolean isBooked = reservationRoomRepository.findActiveReservationForRoom(
                    roomId, createReservationDTO.getCheckInDate()).size() > 0;

            if (isBooked) {
                throw new RuntimeException("Room " + room.getRoomNumber() + " is already booked for these dates");
            }

            rooms.add(room);
        }

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setCheckInDate(createReservationDTO.getCheckInDate());
        reservation.setCheckOutDate(createReservationDTO.getCheckOutDate());
        reservation.setStatus(ReservationStatus.BOOKED);
        reservation.setSpecialRequests(createReservationDTO.getSpecialRequests());
        reservation.setCreatedAt(LocalDateTime.now());

        reservation = reservationRepository.save(reservation);

        // Create reservation-room associations
        for (Room room : rooms) {
            ReservationRoom reservationRoom = new ReservationRoom();
            reservationRoom.setReservation(reservation);
            reservationRoom.setRoom(room);
            reservationRoomRepository.save(reservationRoom);

            // Update room status
            room.setStatus(RoomStatus.OCCUPIED);
            roomRepository.save(room);
        }

        // Fetch the complete reservation with JOIN FETCH to return proper DTO
        Reservation completeReservation = reservationRepository.findByIdWithDetails(reservation.getReservationId())
                .orElseThrow(() -> new RuntimeException("Failed to fetch created reservation"));

        return convertToDTOOptimized(completeReservation);
    }

    // Clear ALL reservation caches when updating
    @Caching(evict = {
            @CacheEvict(value = "reservations", key = "'all'"),
            @CacheEvict(value = "reservations", key = "'reservation_' + #reservationId"),
            @CacheEvict(value = "rooms", allEntries = true)
    })
    @Transactional
    public ReservationDTO updateReservation(Long reservationId, UpdateReservationDTO updateReservationDTO) {
        Reservation reservation = reservationRepository.findByIdWithDetails(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.BOOKED) {
            throw new RuntimeException("Only BOOKED reservations can be modified");
        }

        // Update dates if provided
        if (updateReservationDTO.getNewCheckInDate() != null) {
            if (updateReservationDTO.getNewCheckInDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("New check-in date cannot be in the past");
            }
            reservation.setCheckInDate(updateReservationDTO.getNewCheckInDate());
        }

        if (updateReservationDTO.getNewCheckOutDate() != null) {
            if (updateReservationDTO.getNewCheckOutDate().isBefore(reservation.getCheckInDate())) {
                throw new IllegalArgumentException("Check-out date must be after check-in date");
            }
            reservation.setCheckOutDate(updateReservationDTO.getNewCheckOutDate());
        }

        // Update rooms if provided
        if (updateReservationDTO.getNewRoomIds() != null && !updateReservationDTO.getNewRoomIds().isEmpty()) {
            // Remove old room associations
            List<ReservationRoom> existingRooms = reservationRoomRepository
                    .findByReservationReservationId(reservationId);

            for (ReservationRoom rr : existingRooms) {
                // Set room back to available
                rr.getRoom().setStatus(RoomStatus.AVAILABLE);
                roomRepository.save(rr.getRoom());
                reservationRoomRepository.delete(rr);
            }

            // Add new rooms
            for (Long roomId : updateReservationDTO.getNewRoomIds()) {
                Room room = roomRepository.findById(roomId)
                        .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));

                if (room.getStatus() != RoomStatus.AVAILABLE) {
                    throw new RuntimeException("Room " + room.getRoomNumber() + " is not available");
                }

                ReservationRoom newReservationRoom = new ReservationRoom();
                newReservationRoom.setReservation(reservation);
                newReservationRoom.setRoom(room);
                reservationRoomRepository.save(newReservationRoom);

                room.setStatus(RoomStatus.OCCUPIED);
                roomRepository.save(room);
            }
        }

        // Update special requests
        if (updateReservationDTO.getSpecialRequests() != null) {
            reservation.setSpecialRequests(updateReservationDTO.getSpecialRequests());
        }

        reservation = reservationRepository.save(reservation);

        // Fetch updated reservation with JOIN FETCH
        Reservation updatedReservation = reservationRepository.findByIdWithDetails(reservationId)
                .orElseThrow(() -> new RuntimeException("Failed to fetch updated reservation"));

        return convertToDTOOptimized(updatedReservation);
    }

    // Clear ALL reservation caches when cancelling
    @Caching(evict = {
            @CacheEvict(value = "reservations", key = "'all'"),
            @CacheEvict(value = "reservations", key = "'reservation_' + #reservationId"),
            @CacheEvict(value = "rooms", allEntries = true)
    })
    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdWithDetails(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.BOOKED) {
            throw new RuntimeException("Only BOOKED reservations can be cancelled");
        }

        // Update reservation status
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        // Update room statuses back to available
        List<ReservationRoom> reservationRooms = reservationRoomRepository
                .findByReservationReservationId(reservationId);

        for (ReservationRoom rr : reservationRooms) {
            rr.getRoom().setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(rr.getRoom());
        }
    }

    // ========== CHECK-IN / CHECK-OUT OPERATIONS ==========

    // Clear ALL reservation caches on check-in
    @Caching(evict = {
            @CacheEvict(value = "reservations", key = "'all'"),
            @CacheEvict(value = "reservations", key = "'reservation_' + #checkInDTO.reservationId"),
            @CacheEvict(value = "reservations", key = "'guest_' + #checkInDTO.reservationId"),
            @CacheEvict(value = "rooms", allEntries = true)
    })
    @Transactional
    public StayDTO checkIn(CheckInDTO checkInDTO) {
        Reservation reservation = reservationRepository
                .findByReservationIdAndStatus(checkInDTO.getReservationId(), ReservationStatus.BOOKED)
                .orElseThrow(() -> new RuntimeException("No BOOKED reservation found with ID: " + checkInDTO.getReservationId()));

        // The actualCheckIn is already a LocalDateTime (Jackson converts it)
        LocalDateTime actualCheckIn = checkInDTO.getActualCheckIn() != null
                ? checkInDTO.getActualCheckIn()
                : LocalDateTime.now();

        LocalDate checkInDate = actualCheckIn.toLocalDate();
        LocalDate scheduledCheckIn = reservation.getCheckInDate();
        LocalDate scheduledCheckOut = reservation.getCheckOutDate();

        // DEBUG: Print dates to see what's happening
        System.out.println("DEBUG Check-in attempt:");
        System.out.println("  Actual check-in date: " + checkInDate);
        System.out.println("  Scheduled check-in: " + scheduledCheckIn);
        System.out.println("  Scheduled check-out: " + scheduledCheckOut);
        System.out.println("  Actual check-in datetime: " + actualCheckIn);

        // FIXED: More flexible check-in logic
        // Allow check-in from 1 day before scheduled check-in to 1 day after scheduled check-out
        LocalDate earliestAllowed = scheduledCheckIn.minusDays(1);
        LocalDate latestAllowed = scheduledCheckOut.plusDays(1);

        if (checkInDate.isBefore(earliestAllowed)) {
            throw new RuntimeException("Too early to check in. Earliest allowed: " + earliestAllowed +
                    " (1 day before scheduled check-in)");
        }

        if (checkInDate.isAfter(latestAllowed)) {
            throw new RuntimeException("Cannot check-in after: " + latestAllowed +
                    " (1 day after scheduled check-out)");
        }

        // Check if room is still available
        List<ReservationRoom> reservationRooms = reservationRoomRepository
                .findByReservationReservationId(reservation.getReservationId());

        for (ReservationRoom rr : reservationRooms) {
            Room room = rr.getRoom();
            if (room.getStatus() != RoomStatus.OCCUPIED && room.getStatus() != RoomStatus.AVAILABLE) {
                throw new RuntimeException("Room " + room.getRoomNumber() + " is not available (status: " + room.getStatus() + ")");
            }
        }

        // Update reservation status
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservation = reservationRepository.save(reservation);

        // Create stay record
        Stay stay = new Stay();
        stay.setReservation(reservation);
        stay.setActualCheckIn(actualCheckIn);

        stay = stayRepository.save(stay);

        // Update room status to OCCUPIED
        for (ReservationRoom rr : reservationRooms) {
            rr.getRoom().setStatus(RoomStatus.OCCUPIED);
            roomRepository.save(rr.getRoom());
        }

        // Fetch the stay with complete reservation details
        Stay completeStay = stayRepository.findById(stay.getStayId())
                .orElseThrow(() -> new RuntimeException("Failed to fetch stay"));

        return convertToStayDTO(completeStay);
    }

    // Clear ALL reservation caches on check-out
    @Caching(evict = {
            @CacheEvict(value = "reservations", key = "'all'"),
            @CacheEvict(value = "reservations", key = "'reservation_' + #checkOutDTO.reservationId"),
            @CacheEvict(value = "reservations", key = "'guest_' + #checkOutDTO.reservationId"),
            @CacheEvict(value = "rooms", allEntries = true)
    })
    @Transactional
    public StayDTO checkOut(CheckOutDTO checkOutDTO) {
        // Find the reservation first
        Reservation reservation = reservationRepository
                .findByReservationIdAndStatus(checkOutDTO.getReservationId(), ReservationStatus.CHECKED_IN)
                .orElseThrow(() -> new RuntimeException("No CHECKED_IN reservation found with ID: " + checkOutDTO.getReservationId()));

        // Find the stay for this reservation (1-to-1 relationship)
        Stay stay = stayRepository.findByReservationReservationId(checkOutDTO.getReservationId())
                .orElseThrow(() -> new RuntimeException("No stay record found for reservation: " + checkOutDTO.getReservationId()));

        if (stay.getActualCheckOut() != null) {
            throw new RuntimeException("Guest has already checked out");
        }

        // FIXED: Validate check-out time is after check-in time
        LocalDateTime checkOutTime = checkOutDTO.getActualCheckOut() != null ?
                checkOutDTO.getActualCheckOut() : LocalDateTime.now();

        if (checkOutTime.isBefore(stay.getActualCheckIn())) {
            throw new RuntimeException("Check-out time cannot be before check-in time");
        }

        // FIXED: Allow check-out up to 1 day after scheduled check-out
        LocalDate scheduledCheckOut = reservation.getCheckOutDate();
        LocalDate checkOutDate = checkOutTime.toLocalDate();
        LocalDate latestAllowedCheckOut = scheduledCheckOut.plusDays(1);

        if (checkOutDate.isAfter(latestAllowedCheckOut)) {
            throw new RuntimeException("Check-out is too late. Must check out by " + latestAllowedCheckOut);
        }

        // Update stay
        stay.setActualCheckOut(checkOutTime);
        stay = stayRepository.save(stay);

        // Update reservation status
        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        reservationRepository.save(reservation);

        // Update room statuses back to available
        List<ReservationRoom> reservationRooms = reservationRoomRepository
                .findByReservationReservationId(reservation.getReservationId());

        for (ReservationRoom rr : reservationRooms) {
            rr.getRoom().setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(rr.getRoom());
        }

        // Fetch the updated stay with complete details
        Stay updatedStay = stayRepository.findByIdWithDetails(stay.getStayId())
                .orElseThrow(() -> new RuntimeException("Failed to fetch stay"));

        return convertToStayDTO(updatedStay);
    }

    // ========== ROOM STATUS METHODS ==========

    // Cache room status - USING OPTIMIZED METHOD
    @Cacheable(value = "rooms", key = "'all_hotel_' + #hotelId")
    public List<RoomStatusDTO> getAllRoomStatuses(Long hotelId) {
        System.out.println("⚠️ CACHE MISS: Fetching all room statuses (optimized)...");
        List<Room> rooms;
        if (hotelId != null) {
            rooms = roomRepository.findByHotelIdWithDetails(hotelId);
        } else {
            rooms = roomRepository.findAllWithDetails();
        }

        System.out.println("✅ Fetched " + rooms.size() + " rooms with JOIN FETCH");
        return rooms.stream()
                .map(this::convertToRoomStatusDTOOptimized)
                .collect(Collectors.toList());
    }

    // Cache filtered room status - USING OPTIMIZED METHOD
    @Cacheable(value = "rooms", key = "'status_' + #status + '_hotel_' + #hotelId")
    public List<RoomStatusDTO> getRoomsByStatus(Long hotelId, RoomStatus status) {
        System.out.println("⚠️ CACHE MISS: Fetching rooms by status (optimized)...");
        List<Room> rooms;
        if (hotelId != null) {
            rooms = roomRepository.findByHotelAndStatusWithDetails(hotelId, status);
        } else {
            rooms = roomRepository.findByStatusWithDetails(status);
        }

        System.out.println("✅ Fetched " + rooms.size() + " rooms with status " + status + " using JOIN FETCH");
        return rooms.stream()
                .map(this::convertToRoomStatusDTOOptimized)
                .collect(Collectors.toList());
    }

    // ========== OPTIMIZED DTO CONVERSION METHODS ==========

    // OPTIMIZED DTO conversion (uses pre-fetched data)
    private ReservationDTO convertToDTOOptimized(Reservation reservation) {
        // Create GuestDTO from the already fetched guest (no extra query needed)
        Guest guest = reservation.getGuest();
        GuestDTO guestDTO = new GuestDTO(
                guest.getGuestId(),
                guest.getName(),
                guest.getPhone(),
                guest.getEmail(),
                guest.getIdNumber(),
                guest.getLoyaltyPoints()
        );

        // Get rooms directly from the fetched reservationRooms (no extra queries!)
        List<RoomDTO> roomDTOs = reservation.getReservationRooms().stream()
                .map(rr -> {
                    Room room = rr.getRoom();
                    RoomType roomType = room.getRoomType();
                    RoomTypeDTO roomTypeDTO = new RoomTypeDTO(
                            roomType.getRoomTypeId(),
                            roomType.getName(),
                            roomType.getDescription(),
                            roomType.getCapacity(),
                            roomType.getBaseRate()
                    );

                    return new RoomDTO(
                            room.getRoomId(),
                            room.getRoomNumber(),
                            room.getStatus(),
                            roomTypeDTO,
                            room.getRatePerNight()
                    );
                })
                .collect(Collectors.toList());

        return new ReservationDTO(
                reservation.getReservationId(),
                guestDTO,
                roomDTOs,
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getStatus(),
                reservation.getSpecialRequests(),
                reservation.getCreatedAt()
        );
    }

    private StayDTO convertToStayDTO(Stay stay) {
        // Fetch reservation with JOIN FETCH for the stay
        Reservation reservation = reservationRepository.findByIdWithDetails(stay.getReservation().getReservationId())
                .orElseThrow(() -> new RuntimeException("Failed to fetch reservation for stay"));

        ReservationDTO reservationDTO = convertToDTOOptimized(reservation);

        return new StayDTO(
                stay.getStayId(),
                reservationDTO,
                stay.getActualCheckIn(),
                stay.getActualCheckOut(),
                new ArrayList<>() // Empty service charges for now
        );
    }

    private RoomStatusDTO convertToRoomStatusDTOOptimized(Room room) {
        RoomType roomType = room.getRoomType();
        RoomTypeDTO roomTypeDTO = new RoomTypeDTO(
                roomType.getRoomTypeId(),
                roomType.getName(),
                roomType.getDescription(),
                roomType.getCapacity(),
                roomType.getBaseRate()
        );

        // Find if room is booked and until when
        LocalDate bookedUntil = null;
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            List<ReservationRoom> activeReservations = reservationRoomRepository
                    .findActiveReservationForRoom(room.getRoomId(), LocalDate.now());

            if (!activeReservations.isEmpty()) {
                bookedUntil = activeReservations.get(0).getReservation().getCheckOutDate();
            }
        }

        return new RoomStatusDTO(
                room.getRoomId(),
                room.getRoomNumber(),
                room.getStatus(),
                roomTypeDTO,
                room.getHotel().getName(),
                bookedUntil
        );
    }

    // ========== HELPER METHODS ==========

    public Long getReservationCount() {
        return reservationRepository.count();
    }

    @Cacheable(value = "reservations", key = "'active'")
    public List<ReservationDTO> getActiveReservations() {
        List<Reservation> reservations = reservationRepository.findActiveReservations(LocalDate.now());
        return reservations.stream()
                .map(this::convertToDTOOptimized)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "reservations", key = "'upcoming'")
    public List<ReservationDTO> getUpcomingReservations() {
        List<Reservation> reservations = reservationRepository.findUpcomingReservations(LocalDate.now());
        return reservations.stream()
                .map(this::convertToDTOOptimized)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "reservations", key = "'past'")
    public List<ReservationDTO> getPastReservations() {
        List<Reservation> reservations = reservationRepository.findPastReservations(LocalDate.now());
        return reservations.stream()
                .map(this::convertToDTOOptimized)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "reservations", key = "'today_checkins'")
    public List<ReservationDTO> getTodayCheckIns() {
        List<Reservation> reservations = reservationRepository.findTodayCheckIns(LocalDate.now());
        return reservations.stream()
                .map(this::convertToDTOOptimized)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "reservations", key = "'today_checkouts'")
    public List<ReservationDTO> getTodayCheckOuts() {
        List<Reservation> reservations = reservationRepository.findTodayCheckOuts(LocalDate.now());
        return reservations.stream()
                .map(this::convertToDTOOptimized)
                .collect(Collectors.toList());
    }

    // ========== CACHE CLEARING METHODS ==========

    @CacheEvict(value = "reservations", allEntries = true)
    public void clearAllReservationCaches() {
        System.out.println("🧹 Clearing all reservation caches...");
    }

    @CacheEvict(value = "rooms", allEntries = true)
    public void clearAllRoomCaches() {
        System.out.println("🧹 Clearing all room caches...");
    }

    @CacheEvict(value = {"reservations", "rooms"}, allEntries = true)
    public void clearAllCaches() {
        System.out.println("🧹 Clearing ALL caches...");
    }
}