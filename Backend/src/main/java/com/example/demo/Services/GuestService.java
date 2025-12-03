package com.example.demo.Services;

import com.example.demo.DTOs.GuestDTO;
import com.example.demo.Entities.*;
import com.example.demo.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationRoomRepository reservationRoomRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private StayRepository stayRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    // ========== CACHED READ OPERATIONS ==========

    @Cacheable(value = "guests", key = "'all'")
    public List<GuestDTO> getAllGuests() {
        System.out.println("⚠️ GUEST CACHE MISS: Fetching ALL guests from database...");
        List<Guest> guests = guestRepository.findAll();
        System.out.println("✅ Fetched " + guests.size() + " guests");

        // Debug: Print first few guests
        if (!guests.isEmpty()) {
            System.out.println("📋 Sample guests:");
            for (int i = 0; i < Math.min(3, guests.size()); i++) {
                Guest g = guests.get(i);
                System.out.println("  " + (i+1) + ". ID: " + g.getGuestId() +
                        ", Name: " + g.getName() +
                        ", Email: " + g.getEmail());
            }
        }

        return guests.stream()
                .map(GuestDTO::new)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "guests", key = "'guest_' + #guestId")
    public GuestDTO getGuestById(Long guestId) {
        System.out.println("⚠️ GUEST CACHE MISS: Fetching guest " + guestId + "...");
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found with id: " + guestId));

        // Load reservations to avoid lazy loading issues
        org.hibernate.Hibernate.initialize(guest.getReservations());

        return new GuestDTO(guest);
    }

    @Cacheable(value = "guests", key = "'search_' + #query")
    public List<GuestDTO> searchGuests(String query) {
        System.out.println("⚠️ GUEST CACHE MISS: Searching guests with query: " + query);
        List<Guest> guests = guestRepository.searchGuests(query);
        return guests.stream()
                .map(GuestDTO::new)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "guests", key = "'email_' + #email")
    public GuestDTO getGuestByEmail(String email) {
        System.out.println("⚠️ GUEST CACHE MISS: Fetching guest by email: " + email);
        Guest guest = guestRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Guest not found with email: " + email));
        return new GuestDTO(guest);
    }

    @Cacheable(value = "guests", key = "'idnumber_' + #idNumber")
    public GuestDTO getGuestByIdNumber(String idNumber) {
        System.out.println("⚠️ GUEST CACHE MISS: Fetching guest by ID number: " + idNumber);
        Guest guest = guestRepository.findByIdNumber(idNumber)
                .orElseThrow(() -> new RuntimeException("Guest not found with ID number: " + idNumber));
        return new GuestDTO(guest);
    }

    // ========== CREATE, UPDATE, DELETE OPERATIONS ==========

    @Caching(evict = {
            @CacheEvict(value = "guests", key = "'all'"),
            @CacheEvict(value = "guests", key = "'email_' + #guest.getEmail()", condition = "#guest.getEmail() != null"),
            @CacheEvict(value = "guests", key = "'idnumber_' + #guest.getIdNumber()", condition = "#guest.getIdNumber() != null")
    })
    @Transactional
    public GuestDTO createGuest(Guest guest) {
        System.out.println("📝 Creating new guest: " + guest.getEmail());

        // Validate required fields
        if (guest.getName() == null || guest.getName().trim().isEmpty()) {
            throw new RuntimeException("Guest name is required");
        }

        if (guest.getEmail() == null || guest.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Guest email is required");
        }

        if (guest.getIdNumber() == null || guest.getIdNumber().trim().isEmpty()) {
            throw new RuntimeException("Guest ID number is required");
        }

        // Check if email already exists
        if (guestRepository.findByEmail(guest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + guest.getEmail());
        }

        // Check if ID number already exists
        if (guestRepository.findByIdNumber(guest.getIdNumber()).isPresent()) {
            throw new RuntimeException("ID number already exists: " + guest.getIdNumber());
        }

        // Set default loyalty points if not provided
        if (guest.getLoyaltyPoints() == null) {
            guest.setLoyaltyPoints(0);
        }

        Guest savedGuest = guestRepository.save(guest);
        System.out.println("✅ Guest created successfully with ID: " + savedGuest.getGuestId());

        return new GuestDTO(savedGuest);
    }

    @Caching(evict = {
            @CacheEvict(value = "guests", key = "'all'"),
            @CacheEvict(value = "guests", key = "'guest_' + #guestId"),
            @CacheEvict(value = "guests", key = "'email_' + #guestDetails.getEmail()", condition = "#guestDetails.getEmail() != null"),
            @CacheEvict(value = "guests", key = "'idnumber_' + #guestDetails.getIdNumber()", condition = "#guestDetails.getIdNumber() != null"),
            @CacheEvict(value = "reservations", key = "'guest_' + #guestId")
    })
    @Transactional
    public GuestDTO updateGuest(Long guestId, Guest guestDetails) {
        System.out.println("📝 Updating guest: " + guestId);

        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found with id: " + guestId));

        // Check if new email already exists (excluding current guest)
        if (guestDetails.getEmail() != null && !guestDetails.getEmail().equals(guest.getEmail())) {
            Optional<Guest> existingGuestWithEmail = guestRepository.findByEmail(guestDetails.getEmail());
            if (existingGuestWithEmail.isPresent() && !existingGuestWithEmail.get().getGuestId().equals(guestId)) {
                throw new RuntimeException("Email already exists: " + guestDetails.getEmail());
            }
            guest.setEmail(guestDetails.getEmail());
        }

        // Check if new ID number already exists (excluding current guest)
        if (guestDetails.getIdNumber() != null && !guestDetails.getIdNumber().equals(guest.getIdNumber())) {
            Optional<Guest> existingGuestWithIdNumber = guestRepository.findByIdNumber(guestDetails.getIdNumber());
            if (existingGuestWithIdNumber.isPresent() && !existingGuestWithIdNumber.get().getGuestId().equals(guestId)) {
                throw new RuntimeException("ID number already exists: " + guestDetails.getIdNumber());
            }
            guest.setIdNumber(guestDetails.getIdNumber());
        }

        if (guestDetails.getName() != null) {
            guest.setName(guestDetails.getName());
        }

        if (guestDetails.getPhone() != null) {
            guest.setPhone(guestDetails.getPhone());
        }

        if (guestDetails.getLoyaltyPoints() != null) {
            guest.setLoyaltyPoints(guestDetails.getLoyaltyPoints());
        }

        Guest updatedGuest = guestRepository.save(guest);
        System.out.println("✅ Guest updated successfully: " + guestId);

        return new GuestDTO(updatedGuest);
    }

    // ========== DELETE OPERATIONS ==========

    /**
     * Safe delete - only allows deletion if guest has NO reservations
     */
    @Caching(evict = {
            @CacheEvict(value = "guests", key = "'all'"),
            @CacheEvict(value = "guests", key = "'guest_' + #guestId"),
            @CacheEvict(value = "reservations", key = "'guest_' + #guestId")
    })
    @Transactional
    public void deleteGuest(Long guestId) {
        System.out.println("🗑️ Attempting to delete guest: " + guestId);

        // Load guest with reservations initialized
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found with id: " + guestId));

        // Initialize reservations to avoid lazy loading
        org.hibernate.Hibernate.initialize(guest.getReservations());

        System.out.println("📋 Guest: " + guest.getName() + " has " +
                guest.getReservations().size() + " reservations");

        // Check if guest has ANY reservations
        if (!guest.getReservations().isEmpty()) {
            // Count active reservations
            long activeReservations = guest.getReservations().stream()
                    .filter(r -> r.getStatus() == ReservationStatus.BOOKED ||
                            r.getStatus() == ReservationStatus.CHECKED_IN)
                    .count();

            if (activeReservations > 0) {
                throw new RuntimeException("Cannot delete guest with " + activeReservations +
                        " active reservations. Please cancel them first.");
            }

            throw new RuntimeException("Cannot delete guest with reservation history (" +
                    guest.getReservations().size() + " reservations). " +
                    "Please use archive instead.");
        }

        // Safe to delete (no reservations)
        guestRepository.delete(guest);
        System.out.println("✅ Guest deleted successfully: " + guestId);
    }

    @Caching(evict = {
            @CacheEvict(value = "guests", key = "'all'"),
            @CacheEvict(value = "guests", key = "'guest_' + #guestId")
    })
    @Transactional
    public GuestDTO archiveGuest(Long guestId) {
        System.out.println("📁 Archiving guest: " + guestId);

        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found"));

        // First, cancel any active reservations
        if (guest.getReservations() != null) {
            org.hibernate.Hibernate.initialize(guest.getReservations());

            for (Reservation reservation : guest.getReservations()) {
                if (reservation.getStatus() == ReservationStatus.BOOKED ||
                        reservation.getStatus() == ReservationStatus.CHECKED_IN) {
                    System.out.println("  Cancelling active reservation: " + reservation.getReservationId());
                    reservation.setStatus(ReservationStatus.CANCELLED);
                    reservationRepository.save(reservation);

                    // Free up rooms
                    if (reservation.getReservationRooms() != null) {
                        for (ReservationRoom rr : reservation.getReservationRooms()) {
                            if (rr.getRoom() != null) {
                                rr.getRoom().setStatus(RoomStatus.AVAILABLE);
                                roomRepository.save(rr.getRoom());
                            }
                        }
                    }
                }
            }
        }

        // Mark as archived (you need to add 'archived' field to Guest entity first)
        // guest.setArchived(true);
        // guest = guestRepository.save(guest);

        System.out.println("⚠️ Note: Archive feature requires adding 'archived' field to Guest entity");
        System.out.println("✅ Guest processing completed: " + guestId);

        return new GuestDTO(guest);
    }

    /**
     * Force delete - deletes guest and ALL related data (use with caution!)
     */
    @Caching(evict = {
            @CacheEvict(value = "guests", allEntries = true),
            @CacheEvict(value = "reservations", allEntries = true),
            @CacheEvict(value = "rooms", allEntries = true)
    })
    @Transactional
    public Map<String, Object> forceDeleteGuest(Long guestId) {
        System.out.println("💥 FORCE deleting guest: " + guestId);

        Map<String, Object> result = new HashMap<>();

        // Load guest with all relationships
        Guest guest = guestRepository.findByIdWithAllRelationships(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found with id: " + guestId));

        result.put("guestName", guest.getName());
        result.put("guestEmail", guest.getEmail());

        int deletedReservations = 0;
        int deletedStays = 0;
        int deletedInvoices = 0;

        // Process all reservations
        if (guest.getReservations() != null && !guest.getReservations().isEmpty()) {
            System.out.println("🔄 Processing " + guest.getReservations().size() + " reservations...");

            // Create a copy to avoid ConcurrentModificationException
            List<Reservation> reservations = new ArrayList<>(guest.getReservations());

            for (Reservation reservation : reservations) {
                System.out.println("  Processing reservation ID: " + reservation.getReservationId());

                try {
                    // Handle stay if exists
                    Optional<Stay> stayOpt = stayRepository.findByReservationReservationId(reservation.getReservationId());
                    if (stayOpt.isPresent()) {
                        Stay stay = stayOpt.get();

                        // Delete invoice if exists (will cascade delete payments due to cascade settings)
                        Invoice invoice = stay.getInvoice();
                        if (invoice != null) {
                            // Delete invoice (payments will be deleted automatically if cascade is set)
                            invoiceRepository.delete(invoice);
                            deletedInvoices++;
                        }

                        // Delete stay
                        stayRepository.delete(stay);
                        deletedStays++;
                    }

                    // Delete reservation rooms and update room status
                    if (reservation.getReservationRooms() != null && !reservation.getReservationRooms().isEmpty()) {
                        // Update room statuses back to available
                        for (ReservationRoom rr : reservation.getReservationRooms()) {
                            if (rr.getRoom() != null) {
                                rr.getRoom().setStatus(RoomStatus.AVAILABLE);
                                roomRepository.save(rr.getRoom());
                            }
                        }

                        // Delete reservation rooms
                        reservationRoomRepository.deleteAll(reservation.getReservationRooms());
                    }

                    // Delete reservation
                    reservationRepository.delete(reservation);
                    deletedReservations++;

                } catch (Exception e) {
                    System.out.println("  ❌ Error processing reservation " + reservation.getReservationId() + ": " + e.getMessage());
                    throw new RuntimeException("Failed to process reservation: " + reservation.getReservationId(), e);
                }
            }
        }

        // Clear relationships
        guest.getReservations().clear();

        // Delete guest
        guestRepository.delete(guest);

        result.put("status", "DELETED");
        result.put("deletedReservations", deletedReservations);
        result.put("deletedStays", deletedStays);
        result.put("deletedInvoices", deletedInvoices);
        result.put("message", "Guest and all related data deleted successfully");
        result.put("warning", "This action is irreversible!");

        System.out.println("✅ Force delete completed for guest: " + guestId);
        return result;
    }

    // ========== HELPER METHODS ==========

    @Cacheable(value = "guests", key = "'count'")
    public Long getGuestCount() {
        return guestRepository.count();
    }

    @Cacheable(value = "guests", key = "'sorted_name'")
    public List<GuestDTO> getAllGuestsSortedByName() {
        List<Guest> guests = guestRepository.findAllByOrderByNameAsc();
        return guests.stream()
                .map(GuestDTO::new)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "guests", key = "'sorted_points'")
    public List<GuestDTO> getAllGuestsSortedByPoints() {
        List<Guest> guests = guestRepository.findAllByOrderByLoyaltyPointsDesc();
        return guests.stream()
                .map(GuestDTO::new)
                .collect(Collectors.toList());
    }

    // ========== DEBUG & INFO METHODS ==========

    public Map<String, Object> getGuestInfo(Long guestId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new RuntimeException("Guest not found"));

        Map<String, Object> info = new HashMap<>();
        info.put("guestId", guest.getGuestId());
        info.put("name", guest.getName());
        info.put("email", guest.getEmail());
        info.put("phone", guest.getPhone());
        info.put("loyaltyPoints", guest.getLoyaltyPoints());

        // Reservation info
        List<Map<String, Object>> reservations = new ArrayList<>();
        if (guest.getReservations() != null) {
            org.hibernate.Hibernate.initialize(guest.getReservations());
            for (Reservation r : guest.getReservations()) {
                Map<String, Object> res = new HashMap<>();
                res.put("reservationId", r.getReservationId());
                res.put("status", r.getStatus());
                res.put("checkIn", r.getCheckInDate());
                res.put("checkOut", r.getCheckOutDate());
                res.put("rooms", r.getReservationRooms() != null ? r.getReservationRooms().size() : 0);
                reservations.add(res);
            }
        }

        info.put("totalReservations", reservations.size());
        info.put("reservations", reservations);

        return info;
    }

    // ========== CACHE CLEARING METHODS ==========

    @CacheEvict(value = "guests", allEntries = true)
    public void clearAllGuestCaches() {
        System.out.println("🧹 Clearing all guest caches...");
    }

    @CacheEvict(value = {"guests", "reservations"}, allEntries = true)
    public void clearAllRelatedCaches() {
        System.out.println("🧹 Clearing guest and reservation caches...");
    }
}