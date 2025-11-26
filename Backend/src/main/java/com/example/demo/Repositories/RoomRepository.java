package com.example.demo.Repositories;

import com.example.demo.Entities.Room;
import com.example.demo.Entities.RoomStatus;
import com.example.demo.Entities.RoomType;
import com.example.demo.Entities.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotel(Hotel hotel);
    List<Room> findByRoomType(RoomType roomType);
    List<Room> findByStatus(RoomStatus status);
    Room findByRoomNumber(String roomNumber);

    // Count rooms by room type for analytics
    long countByRoomType(RoomType roomType);

    // Count rooms by room type and hotel
    @Query("SELECT COUNT(r) FROM Room r WHERE r.roomType = :roomType AND r.hotel.hotelId = :hotelId")
    long countByRoomTypeAndHotelId(@Param("roomType") RoomType roomType, @Param("hotelId") Long hotelId);

    // Count rooms by status for analytics
    long countByStatus(RoomStatus status);

    // Count rooms by hotel and status
    @Query("SELECT COUNT(r) FROM Room r WHERE r.hotel.hotelId = :hotelId AND r.status = :status")
    long countByHotelIdAndStatus(@Param("hotelId") Long hotelId, @Param("status") RoomStatus status);

    // Find available rooms for a specific hotel
    List<Room> findByHotelAndStatus(Hotel hotel, RoomStatus status);

    // Get room count by hotel
    @Query("SELECT COUNT(r) FROM Room r WHERE r.hotel.hotelId = :hotelId")
    long countByHotelId(@Param("hotelId") Long hotelId);

    // Get room count by hotel entity
    long countByHotel(Hotel hotel);

    // Get occupancy rate by hotel
    @Query("SELECT COUNT(r) FROM Room r WHERE r.hotel.hotelId = :hotelId AND r.status = com.example.demo.Entities.RoomStatus.OCCUPIED")
    long countOccupiedRoomsByHotelId(@Param("hotelId") Long hotelId);

    // Get occupied rooms by hotel entity
    @Query("SELECT COUNT(r) FROM Room r WHERE r.hotel = :hotel AND r.status = com.example.demo.Entities.RoomStatus.OCCUPIED")
    long countOccupiedRoomsByHotel(@Param("hotel") Hotel hotel);

    // Get rooms that are either available or occupied (for occupancy calculation)
    @Query("SELECT r FROM Room r WHERE r.status IN (com.example.demo.Entities.RoomStatus.AVAILABLE, com.example.demo.Entities.RoomStatus.OCCUPIED)")
    List<Room> findActiveRooms();

    // Get room statistics summary
    @Query("SELECT r.status, COUNT(r) FROM Room r GROUP BY r.status")
    List<Object[]> getRoomStatusSummary();

    // Get room statistics summary by hotel
    @Query("SELECT r.status, COUNT(r) FROM Room r WHERE r.hotel.hotelId = :hotelId GROUP BY r.status")
    List<Object[]> getRoomStatusSummaryByHotel(@Param("hotelId") Long hotelId);
}