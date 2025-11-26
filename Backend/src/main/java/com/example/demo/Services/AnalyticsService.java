package com.example.demo.Services;

import com.example.demo.Repositories.*;
import com.example.demo.Entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class AnalyticsService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ServiceChargeRepository serviceChargeRepository;

    @Autowired
    private StayRepository stayRepository;

    @Autowired
    private HotelRepository hotelRepository;

    public List<Map<String, Object>> getRoomTypeDistribution(Long hotelId) {
        List<Map<String, Object>> data = new ArrayList<>();

        List<RoomType> roomTypes = roomTypeRepository.findAll();
        for (RoomType roomType : roomTypes) {
            long roomCount;
            if (hotelId != null) {
                roomCount = roomRepository.countByRoomTypeAndHotelId(roomType, hotelId);
            } else {
                roomCount = roomRepository.countByRoomType(roomType);
            }

            Map<String, Object> roomTypeData = new HashMap<>();
            roomTypeData.put("name", roomType.getName());
            roomTypeData.put("value", roomCount);
            data.add(roomTypeData);
        }

        return data;
    }

    public List<Map<String, Object>> getServiceRevenue(Long hotelId) {
        List<Map<String, Object>> data = new ArrayList<>();

        List<ServiceCharge> allCharges;
        if (hotelId != null) {
            allCharges = serviceChargeRepository.findByHotelId(hotelId);
        } else {
            allCharges = serviceChargeRepository.findAll();
        }

        Map<String, Double> serviceRevenueMap = new HashMap<>();

        for (ServiceCharge charge : allCharges) {
            String serviceType = charge.getServiceType();
            double amount = charge.getAmount().doubleValue();
            serviceRevenueMap.merge(serviceType, amount, Double::sum);
        }

        for (Map.Entry<String, Double> entry : serviceRevenueMap.entrySet()) {
            Map<String, Object> serviceData = new HashMap<>();
            serviceData.put("service", entry.getKey());
            serviceData.put("revenue", entry.getValue());
            data.add(serviceData);
        }

        return data;
    }

    public List<Map<String, Object>> getHotelRevenue() {
        List<Map<String, Object>> data = new ArrayList<>();

        List<Hotel> hotels = hotelRepository.findAll();
        for (Hotel hotel : hotels) {
            double hotelRevenue = calculateHotelRevenue(hotel);
            Map<String, Object> hotelData = new HashMap<>();
            hotelData.put("hotel", hotel.getName());
            hotelData.put("revenue", hotelRevenue);
            data.add(hotelData);
        }

        return data;
    }

    private double calculateHotelRevenue(Hotel hotel) {
        Double totalRevenue = invoiceRepository.findTotalRevenueByHotelAndPeriod(
                hotel.getId(), LocalDate.of(2020, 1, 1), LocalDate.now()
        );
        return totalRevenue != null ? totalRevenue : 0.0;
    }

    public Map<String, Object> getKeyMetrics(Long hotelId) {
        Map<String, Object> metrics = new HashMap<>();

        // Total Revenue
        Double totalRevenue;
        if (hotelId != null) {
            totalRevenue = invoiceRepository.findTotalRevenueByHotelAndPeriod(
                    hotelId, LocalDate.of(2020, 1, 1), LocalDate.now()
            );
        } else {
            totalRevenue = invoiceRepository.findTotalRevenueByPeriod(
                    LocalDate.of(2020, 1, 1), LocalDate.now()
            );
        }
        metrics.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

        // Total Guests
        long totalGuests;
        if (hotelId != null) {
            totalGuests = guestRepository.countByHotelId(hotelId);
        } else {
            totalGuests = guestRepository.count();
        }
        metrics.put("totalGuests", totalGuests);

        // Room Statistics
        long totalRooms, availableRooms, occupiedRooms, maintenanceRooms;
        if (hotelId != null) {
            totalRooms = roomRepository.countByHotelId(hotelId);
            availableRooms = roomRepository.countByHotelIdAndStatus(hotelId, RoomStatus.AVAILABLE);
            occupiedRooms = roomRepository.countByHotelIdAndStatus(hotelId, RoomStatus.OCCUPIED);
            maintenanceRooms = roomRepository.countByHotelIdAndStatus(hotelId, RoomStatus.MAINTENANCE);
        } else {
            totalRooms = roomRepository.count();
            availableRooms = roomRepository.countByStatus(RoomStatus.AVAILABLE);
            occupiedRooms = roomRepository.countByStatus(RoomStatus.OCCUPIED);
            maintenanceRooms = roomRepository.countByStatus(RoomStatus.MAINTENANCE);
        }

        metrics.put("totalRooms", totalRooms);
        metrics.put("availableRooms", availableRooms);
        metrics.put("occupiedRooms", occupiedRooms);
        metrics.put("maintenanceRooms", maintenanceRooms);

        // Occupancy Rate
        double occupancyRate = totalRooms > 0 ? (occupiedRooms * 100.0) / totalRooms : 0;
        metrics.put("occupancyRate", Math.round(occupancyRate));

        // Average Daily Rate (ADR)
        double adr = totalRevenue != null && occupiedRooms > 0 ? totalRevenue / occupiedRooms : 0;
        metrics.put("averageDailyRate", Math.round(adr * 100.0) / 100.0);

        // Average Loyalty Points
        Double avgLoyaltyPoints;
        if (hotelId != null) {
            avgLoyaltyPoints = guestRepository.findAverageLoyaltyPointsByHotelId(hotelId);
        } else {
            avgLoyaltyPoints = guestRepository.findAverageLoyaltyPoints();
        }
        metrics.put("averageLoyaltyPoints", avgLoyaltyPoints != null ? Math.round(avgLoyaltyPoints) : 0);

        // VIP Guests
        long vipGuests;
        if (hotelId != null) {
            vipGuests = guestRepository.countVIPGuestsByHotelId(hotelId);
        } else {
            vipGuests = guestRepository.countVIPGuests();
        }
        metrics.put("vipGuests", vipGuests);

        return metrics;
    }

    public Map<String, Object> getOccupancyRate(Long hotelId) {
        Map<String, Object> occupancy = new HashMap<>();

        long totalRooms, occupiedRooms, availableRooms;
        if (hotelId != null) {
            totalRooms = roomRepository.countByHotelId(hotelId);
            occupiedRooms = roomRepository.countByHotelIdAndStatus(hotelId, RoomStatus.OCCUPIED);
            availableRooms = roomRepository.countByHotelIdAndStatus(hotelId, RoomStatus.AVAILABLE);
        } else {
            totalRooms = roomRepository.count();
            occupiedRooms = roomRepository.countByStatus(RoomStatus.OCCUPIED);
            availableRooms = roomRepository.countByStatus(RoomStatus.AVAILABLE);
        }

        double occupancyRate = totalRooms > 0 ? (occupiedRooms * 100.0) / totalRooms : 0;

        occupancy.put("currentOccupancy", Math.round(occupancyRate));
        occupancy.put("availableRooms", availableRooms);
        occupancy.put("occupiedRooms", occupiedRooms);
        occupancy.put("totalRooms", totalRooms);

        return occupancy;
    }

    public Map<String, Object> getGuestStatistics(Long hotelId) {
        Map<String, Object> guestStats = new HashMap<>();

        long totalGuests;
        Double avgLoyaltyPoints;
        long vipGuests;

        if (hotelId != null) {
            totalGuests = guestRepository.countByHotelId(hotelId);
            avgLoyaltyPoints = guestRepository.findAverageLoyaltyPointsByHotelId(hotelId);
            vipGuests = guestRepository.countVIPGuestsByHotelId(hotelId);
        } else {
            totalGuests = guestRepository.count();
            avgLoyaltyPoints = guestRepository.findAverageLoyaltyPoints();
            vipGuests = guestRepository.countVIPGuests();
        }

        guestStats.put("totalGuests", totalGuests);
        guestStats.put("averageLoyaltyPoints", avgLoyaltyPoints != null ? Math.round(avgLoyaltyPoints) : 0);
        guestStats.put("vipGuests", vipGuests);

        return guestStats;
    }

    public Map<String, Object> getReservationStatistics(Long hotelId) {
        Map<String, Object> reservationStats = new HashMap<>();

        List<Reservation> allReservations;
        if (hotelId != null) {
            allReservations = reservationRepository.findByHotelId(hotelId);
        } else {
            allReservations = reservationRepository.findAll();
        }

        long totalReservations = allReservations.size();
        long completedStays = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_OUT)
                .count();
        long currentStays = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN)
                .count();
        long futureBookings = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.BOOKED)
                .count();
        long cancellations = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                .count();
        long noShows = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.NO_SHOW)
                .count();

        double cancellationRate = totalReservations > 0 ?
                (cancellations * 100.0) / totalReservations : 0;

        reservationStats.put("totalReservations", totalReservations);
        reservationStats.put("completedStays", completedStays);
        reservationStats.put("currentStays", currentStays);
        reservationStats.put("futureBookings", futureBookings);
        reservationStats.put("cancellations", cancellations);
        reservationStats.put("noShows", noShows);
        reservationStats.put("cancellationRate", Math.round(cancellationRate));

        return reservationStats;
    }
}