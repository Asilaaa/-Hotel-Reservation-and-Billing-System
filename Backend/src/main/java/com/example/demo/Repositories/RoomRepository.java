package com.example.demo.Repositories;

import com.example.demo.Entities.Room;
import com.example.demo.Entities.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByStatus(RoomStatus status);

    List<Room> findByRoomTypeRoomTypeId(Long roomTypeId);

    List<Room> findByHotelHotelId(Long hotelId);

    @Query("SELECT r FROM Room r WHERE r.status = 'AVAILABLE' AND " +
            "r.roomId NOT IN (SELECT rr.room.roomId FROM ReservationRoom rr " +
            "JOIN rr.reservation res WHERE res.status = 'BOOKED' AND " +
            "res.checkInDate <= :checkOutDate AND res.checkOutDate >= :checkInDate)")
    List<Room> findAvailableRooms(@Param("checkInDate") LocalDate checkInDate,
                                  @Param("checkOutDate") LocalDate checkOutDate);

    @Query("SELECT r FROM Room r WHERE r.hotel.hotelId = :hotelId AND r.status = :status")
    List<Room> findByHotelAndStatus(@Param("hotelId") Long hotelId, @Param("status") RoomStatus status);


    @Query("SELECT DISTINCT r FROM Room r " +
            "LEFT JOIN FETCH r.roomType " +
            "LEFT JOIN FETCH r.hotel " +
            "WHERE r.hotel.hotelId = :hotelId")
    List<Room> findByHotelIdWithDetails(@Param("hotelId") Long hotelId);

    @Query("SELECT DISTINCT r FROM Room r " +
            "LEFT JOIN FETCH r.roomType " +
            "LEFT JOIN FETCH r.hotel")
    List<Room> findAllWithDetails();

    @Query("SELECT DISTINCT r FROM Room r " +
            "LEFT JOIN FETCH r.roomType " +
            "LEFT JOIN FETCH r.hotel " +
            "WHERE r.hotel.hotelId = :hotelId AND r.status = :status")
    List<Room> findByHotelAndStatusWithDetails(@Param("hotelId") Long hotelId, @Param("status") RoomStatus status);

    @Query("SELECT DISTINCT r FROM Room r " +
            "LEFT JOIN FETCH r.roomType " +
            "LEFT JOIN FETCH r.hotel " +
            "WHERE r.status = :status")
    List<Room> findByStatusWithDetails(@Param("status") RoomStatus status);
}