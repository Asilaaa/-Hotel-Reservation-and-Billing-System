package com.example.demo.Repositories;

import com.example.demo.Entities.Stay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StayRepository extends JpaRepository<Stay, Long> {
    List<Stay> findByActualCheckOutIsNull();


    @Query("SELECT s FROM Stay s WHERE s.actualCheckIn >= :startDate AND s.actualCheckIn < :endDate")
    List<Stay> findStaysBetweenDates(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    Optional<Stay> findByReservationReservationId(Long reservationId);

    // ADD THIS OPTIMIZED METHOD:
    @Query("SELECT DISTINCT s FROM Stay s " +
            "LEFT JOIN FETCH s.reservation r " +
            "LEFT JOIN FETCH r.guest " +
            "LEFT JOIN FETCH r.reservationRooms rr " +
            "LEFT JOIN FETCH rr.room rm " +
            "LEFT JOIN FETCH rm.roomType " +
            "LEFT JOIN FETCH rm.hotel " +
            "WHERE s.stayId = :stayId")
    Optional<Stay> findByIdWithDetails(@Param("stayId") Long stayId);

    // Also add this if you want optimized findByReservationReservationId:
    @Query("SELECT DISTINCT s FROM Stay s " +
            "LEFT JOIN FETCH s.reservation r " +
            "LEFT JOIN FETCH r.guest " +
            "LEFT JOIN FETCH r.reservationRooms rr " +
            "LEFT JOIN FETCH rr.room rm " +
            "LEFT JOIN FETCH rm.roomType " +
            "LEFT JOIN FETCH rm.hotel " +
            "WHERE r.reservationId = :reservationId")
    Optional<Stay> findByReservationIdWithDetails(@Param("reservationId") Long reservationId);
}