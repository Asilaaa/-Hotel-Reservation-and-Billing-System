package com.example.demo.Repositories;

import com.example.demo.Entities.RevenueStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RevenueStatsRepository extends JpaRepository<RevenueStats, Long> {

    List<RevenueStats> findByHotelHotelIdAndStatDateBetween(Long hotelId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM RevenueStats r WHERE r.hotel.hotelId = :hotelId AND r.statDate BETWEEN :startDate AND :endDate ORDER BY r.statDate")
    List<RevenueStats> findDailyRevenueByHotelAndDateRange(@Param("hotelId") Long hotelId,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);

    @Query("SELECT YEAR(r.statDate), MONTH(r.statDate), SUM(r.totalRevenue) " +
            "FROM RevenueStats r WHERE r.hotel.hotelId = :hotelId AND r.statDate BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(r.statDate), MONTH(r.statDate) " +
            "ORDER BY YEAR(r.statDate), MONTH(r.statDate)")
    List<Object[]> findMonthlyRevenueByHotelAndDateRange(@Param("hotelId") Long hotelId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    @Query("SELECT YEAR(r.statDate), SUM(r.totalRevenue) " +
            "FROM RevenueStats r WHERE r.hotel.hotelId = :hotelId AND r.statDate BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(r.statDate) " +
            "ORDER BY YEAR(r.statDate)")
    List<Object[]> findYearlyRevenueByHotelAndDateRange(@Param("hotelId") Long hotelId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    // Fixed query - use IS NULL check for "all hotels" case
    @Query("SELECT r FROM RevenueStats r WHERE " +
            "(:hotelId IS NULL OR r.hotel.hotelId = :hotelId) " +
            "AND r.periodType = :periodType " +
            "AND r.statDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.statDate")
    List<RevenueStats> findByHotelAndPeriodType(@Param("hotelId") Long hotelId,
                                                @Param("periodType") String periodType,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM RevenueStats r WHERE " +
            "r.periodType = :periodType " +
            "AND r.statDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.statDate")
    List<RevenueStats> findByPeriodType(@Param("periodType") String periodType,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
}