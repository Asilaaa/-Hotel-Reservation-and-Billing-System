package com.example.demo.Repositories;

import com.example.demo.Entities.HotelRevenueStats;
import com.example.demo.Entities.PeriodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRevenueStatsRepository extends JpaRepository<HotelRevenueStats, Long> {

    // Find by hotel and period
    Optional<HotelRevenueStats> findByHotelHotelIdAndStatPeriodAndPeriodType(
            Long hotelId, LocalDate statPeriod, PeriodType periodType);

    // Find all stats for a hotel
    List<HotelRevenueStats> findByHotelHotelId(Long hotelId);

    // Find yearly stats for a hotel
    List<HotelRevenueStats> findByHotelHotelIdAndPeriodTypeOrderByStatPeriodAsc(
            Long hotelId, PeriodType periodType);

    // Find monthly stats for a hotel and year
    @Query("SELECT s FROM HotelRevenueStats s " +
            "WHERE s.hotel.hotelId = :hotelId " +
            "AND s.periodType = 'monthly' " +  // Changed to lowercase
            "AND YEAR(s.statPeriod) = :year " +
            "ORDER BY s.statPeriod ASC")
    List<HotelRevenueStats> findMonthlyStatsByHotelAndYear(
            @Param("hotelId") Long hotelId,
            @Param("year") Integer year);

    // Find yearly stats for all hotels or specific hotel
    @Query("SELECT s FROM HotelRevenueStats s " +
            "WHERE s.periodType = 'yearly' " +  // Changed to lowercase
            "AND (:hotelId IS NULL OR s.hotel.hotelId = :hotelId) " +
            "AND YEAR(s.statPeriod) BETWEEN 2022 AND 2025 " +
            "ORDER BY s.statPeriod ASC")
    List<HotelRevenueStats> findYearlyStats(@Param("hotelId") Long hotelId);

    // Find monthly stats for all hotels or specific hotel for a year
    @Query("SELECT s FROM HotelRevenueStats s " +
            "WHERE s.periodType = 'monthly' " +  // Changed to lowercase
            "AND (:hotelId IS NULL OR s.hotel.hotelId = :hotelId) " +
            "AND YEAR(s.statPeriod) = :year " +
            "ORDER BY s.statPeriod ASC")
    List<HotelRevenueStats> findMonthlyStatsByYear(
            @Param("hotelId") Long hotelId,
            @Param("year") Integer year);

    // Delete old stats for a period (for recalculation)
    @Query("DELETE FROM HotelRevenueStats s " +
            "WHERE s.hotel.hotelId = :hotelId " +
            "AND s.periodType = :periodType " +
            "AND YEAR(s.statPeriod) = :year " +
            "AND MONTH(s.statPeriod) = :month")
    void deleteStatsForPeriod(
            @Param("hotelId") Long hotelId,
            @Param("periodType") PeriodType periodType,
            @Param("year") Integer year,
            @Param("month") Integer month);
}