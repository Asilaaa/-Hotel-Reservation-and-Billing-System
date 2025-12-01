package com.example.demo.Repositories;

import com.example.demo.Entities.ServiceChargeStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ServiceChargeStatsRepository extends JpaRepository<ServiceChargeStats, Long> {

    List<ServiceChargeStats> findByHotelHotelIdAndStatDateBetween(Long hotelId, LocalDate startDate, LocalDate endDate);
    List<ServiceChargeStats> findByStatDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT s.serviceType, SUM(s.totalAmount), SUM(s.countUsed) " +
            "FROM ServiceChargeStats s WHERE s.hotel.hotelId = :hotelId AND s.statDate BETWEEN :startDate AND :endDate " +
            "GROUP BY s.serviceType " +
            "ORDER BY SUM(s.totalAmount) DESC")
    List<Object[]> findServiceSummaryByHotelAndDateRange(@Param("hotelId") Long hotelId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);
}