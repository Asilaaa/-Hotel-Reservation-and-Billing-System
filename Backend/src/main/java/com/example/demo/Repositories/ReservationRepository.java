package com.example.demo.Repositories;

import com.example.demo.Entities.Reservation;
import com.example.demo.Entities.Guest;
import com.example.demo.Entities.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByGuest(Guest guest);
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByCheckInDateBetween(LocalDate start, LocalDate end);
    List<Reservation> findByCheckOutDateBetween(LocalDate start, LocalDate end);

    // For analytics - find reservations by specific check-in date
    List<Reservation> findByCheckInDate(LocalDate checkInDate);

    // Find reservations by hotel
    @Query("SELECT r FROM Reservation r " +
            "JOIN r.reservationRooms rr " +
            "JOIN rr.room rm " +
            "WHERE rm.hotel.hotelId = :hotelId")
    List<Reservation> findByHotelId(@Param("hotelId") Long hotelId);

    // Get reservation count by status
    @Query("SELECT r.status, COUNT(r) FROM Reservation r GROUP BY r.status")
    List<Object[]> getReservationStatusSummary();

    // Get reservation count by status for specific hotel
    @Query("SELECT r.status, COUNT(r) FROM Reservation r " +
            "JOIN r.reservationRooms rr " +
            "JOIN rr.room rm " +
            "WHERE rm.hotel.hotelId = :hotelId " +
            "GROUP BY r.status")
    List<Object[]> getReservationStatusSummaryByHotel(@Param("hotelId") Long hotelId);

    // Get today's check-ins
    @Query("SELECT r FROM Reservation r WHERE r.checkInDate = :today AND r.status = 'BOOKED'")
    List<Reservation> findTodayCheckIns(@Param("today") LocalDate today);

    // Get today's check-outs
    @Query("SELECT r FROM Reservation r WHERE r.checkOutDate = :today AND r.status = 'CHECKED_IN'")
    List<Reservation> findTodayCheckOuts(@Param("today") LocalDate today);

    // Get active reservations for a hotel (checked in)
    @Query("SELECT r FROM Reservation r " +
            "JOIN r.reservationRooms rr " +
            "JOIN rr.room rm " +
            "WHERE rm.hotel.hotelId = :hotelId AND r.status = 'CHECKED_IN'")
    List<Reservation> findActiveReservationsByHotel(@Param("hotelId") Long hotelId);

    // Get upcoming reservations for a hotel
    @Query("SELECT r FROM Reservation r " +
            "JOIN r.reservationRooms rr " +
            "JOIN rr.room rm " +
            "WHERE rm.hotel.hotelId = :hotelId AND r.status = 'BOOKED' AND r.checkInDate >= :today")
    List<Reservation> findUpcomingReservationsByHotel(@Param("hotelId") Long hotelId,
                                                      @Param("today") LocalDate today);
}