package com.example.demo.Repositories;

import com.example.demo.Entities.Reservation;
import com.example.demo.Entities.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByGuestGuestId(Long guestId);

    List<Reservation> findByStatus(ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.status = 'BOOKED' AND r.checkInDate = :date")
    List<Reservation> findTodayCheckIns(@Param("date") LocalDate date);

    @Query("SELECT r FROM Reservation r WHERE r.status = 'CHECKED_IN' AND r.checkOutDate = :date")
    List<Reservation> findTodayCheckOuts(@Param("date") LocalDate date);

    Optional<Reservation> findByReservationIdAndStatus(Long reservationId, ReservationStatus status);

    @Query("SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.guest " +
            "LEFT JOIN FETCH r.reservationRooms rr " +
            "LEFT JOIN FETCH rr.room rm " +
            "LEFT JOIN FETCH rm.roomType " +
            "LEFT JOIN FETCH rm.hotel " +
            "ORDER BY r.createdAt DESC")
    List<Reservation> findAllWithDetails();

    @Query("SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.guest " +
            "LEFT JOIN FETCH r.reservationRooms rr " +
            "LEFT JOIN FETCH rr.room rm " +
            "LEFT JOIN FETCH rm.roomType " +
            "LEFT JOIN FETCH rm.hotel " +
            "WHERE r.guest.guestId = :guestId " +
            "ORDER BY r.createdAt DESC")
    List<Reservation> findByGuestIdWithDetails(@Param("guestId") Long guestId);

    @Query("SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.guest " +
            "LEFT JOIN FETCH r.reservationRooms rr " +
            "LEFT JOIN FETCH rr.room rm " +
            "LEFT JOIN FETCH rm.roomType " +
            "LEFT JOIN FETCH rm.hotel " +
            "WHERE r.reservationId = :reservationId")
    Optional<Reservation> findByIdWithDetails(@Param("reservationId") Long reservationId);

    // Add these missing methods:
    @Query("SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.guest " +
            "LEFT JOIN FETCH r.reservationRooms rr " +
            "LEFT JOIN FETCH rr.room rm " +
            "LEFT JOIN FETCH rm.roomType " +
            "LEFT JOIN FETCH rm.hotel " +
            "WHERE (r.status = 'BOOKED' OR r.status = 'CHECKED_IN') " +
            "AND (:today BETWEEN r.checkInDate AND r.checkOutDate) " +
            "ORDER BY r.checkInDate ASC")
    List<Reservation> findActiveReservations(@Param("today") LocalDate today);

    @Query("SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.guest " +
            "LEFT JOIN FETCH r.reservationRooms rr " +
            "LEFT JOIN FETCH rr.room rm " +
            "LEFT JOIN FETCH rm.roomType " +
            "LEFT JOIN FETCH rm.hotel " +
            "WHERE r.status = 'BOOKED' " +
            "AND r.checkInDate > :today " +
            "ORDER BY r.checkInDate ASC")
    List<Reservation> findUpcomingReservations(@Param("today") LocalDate today);

    @Query("SELECT DISTINCT r FROM Reservation r " +
            "LEFT JOIN FETCH r.guest " +
            "LEFT JOIN FETCH r.reservationRooms rr " +
            "LEFT JOIN FETCH rr.room rm " +
            "LEFT JOIN FETCH rm.roomType " +
            "LEFT JOIN FETCH rm.hotel " +
            "WHERE (r.status = 'CHECKED_OUT' OR r.status = 'CANCELLED') " +
            "AND r.checkOutDate < :today " +
            "ORDER BY r.checkOutDate DESC")
    List<Reservation> findPastReservations(@Param("today") LocalDate today);
}