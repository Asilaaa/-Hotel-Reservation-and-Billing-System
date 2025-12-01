package com.example.demo.Repositories;

import com.example.demo.Entities.HotelOccupancyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelOccupancyStatsRepository extends JpaRepository<HotelOccupancyStats, Long> {

    // Find yearly stats for a specific hotel
    @Query("SELECT h FROM HotelOccupancyStats h WHERE h.hotel.hotelId = :hotelId AND h.periodType = 'yearly' ORDER BY h.periodDate")
    List<HotelOccupancyStats> findYearlyStatsByHotelId(@Param("hotelId") Long hotelId);

    // Find yearly stats for all hotels
    @Query("SELECT h FROM HotelOccupancyStats h WHERE h.periodType = 'yearly' ORDER BY h.hotel.hotelId, h.periodDate")
    List<HotelOccupancyStats> findAllYearlyStats();

    // Find monthly stats for a specific hotel and year
    @Query("SELECT h FROM HotelOccupancyStats h WHERE h.hotel.hotelId = :hotelId AND h.periodType = 'monthly' AND EXTRACT(YEAR FROM h.periodDate) = :year ORDER BY h.periodDate")
    List<HotelOccupancyStats> findMonthlyStatsByHotelIdAndYear(@Param("hotelId") Long hotelId, @Param("year") Integer year);

    // Find monthly stats for all hotels for a specific year
    @Query("SELECT h FROM HotelOccupancyStats h WHERE h.periodType = 'monthly' AND EXTRACT(YEAR FROM h.periodDate) = :year ORDER BY h.hotel.hotelId, h.periodDate")
    List<HotelOccupancyStats> findMonthlyStatsByYear(@Param("year") Integer year);
}