package com.example.demo.Repositories;

import com.example.demo.Entities.ReservationRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoom, Long> {

    List<ReservationRoom> findByReservationReservationId(Long reservationId);

    @Query("SELECT rr FROM ReservationRoom rr WHERE rr.room.roomId = :roomId AND " +
            "rr.reservation.status = 'BOOKED' AND " +
            "rr.reservation.checkInDate <= :date AND rr.reservation.checkOutDate > :date")
    List<ReservationRoom> findActiveReservationForRoom(@Param("roomId") Long roomId,
                                                       @Param("date") LocalDate date);
}