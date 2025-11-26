package com.example.demo.Repositories;

import com.example.demo.Entities.ReservationRoom;
import com.example.demo.Entities.Reservation;
import com.example.demo.Entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoom, Long> {
    List<ReservationRoom> findByReservation(Reservation reservation);
    List<ReservationRoom> findByRoom(Room room);
    void deleteByReservation(Reservation reservation);
}