package com.example.demo.Repositories;

import com.example.demo.Entities.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    RoomType findByName(String name);

    // Find room types by capacity range
    List<RoomType> findByCapacityBetween(Integer minCapacity, Integer maxCapacity);

    // Find room types with base rate above a certain amount
    List<RoomType> findByBaseRateGreaterThan(Double minRate);

    // Get room type statistics for analytics
    @Query("SELECT rt.name, COUNT(r), AVG(r.ratePerNight), SUM(r.ratePerNight) " +
            "FROM RoomType rt LEFT JOIN rt.rooms r " +
            "GROUP BY rt.name, rt.roomTypeId")
    List<Object[]> getRoomTypeStatistics();

    // Get popular room types (most booked)
    @Query("SELECT rt.name, COUNT(rr) as bookingCount " +
            "FROM RoomType rt " +
            "JOIN rt.rooms r " +
            "JOIN r.reservationRooms rr " +
            "GROUP BY rt.name, rt.roomTypeId " +
            "ORDER BY bookingCount DESC")
    List<Object[]> getPopularRoomTypes();

    // Get room type revenue statistics
    @Query("SELECT rt.name, SUM(i.totalAmount) as totalRevenue " +
            "FROM RoomType rt " +
            "JOIN rt.rooms r " +
            "JOIN r.reservationRooms rr " +
            "JOIN rr.reservation res " +
            "JOIN res.stay s " +
            "JOIN s.invoice i " +
            "WHERE i.totalAmount > 0 " +
            "GROUP BY rt.name, rt.roomTypeId " +
            "ORDER BY totalRevenue DESC")
    List<Object[]> getRoomTypeRevenue();

    // Find room types with availability
    @Query("SELECT DISTINCT rt FROM RoomType rt " +
            "JOIN rt.rooms r " +
            "WHERE r.status = 'AVAILABLE'")
    List<RoomType> findAvailableRoomTypes();

    // Get average daily rate by room type
    @Query("SELECT rt.name, AVG(r.ratePerNight) as avgRate " +
            "FROM RoomType rt " +
            "JOIN rt.rooms r " +
            "WHERE r.ratePerNight > 0 " +
            "GROUP BY rt.name, rt.roomTypeId")
    List<Object[]> getAverageRatesByRoomType();
}