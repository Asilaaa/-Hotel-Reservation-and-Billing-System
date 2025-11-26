package com.example.demo.Repositories;

import com.example.demo.Entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i.issueDate, SUM(i.totalAmount) FROM Invoice i " +
            "WHERE i.issueDate BETWEEN :startDate AND :endDate " +
            "GROUP BY i.issueDate " +
            "ORDER BY i.issueDate")
    List<Object[]> findDailyRevenue(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT EXTRACT(YEAR FROM issue_date) as year, " +
            "EXTRACT(MONTH FROM issue_date) as month, " +
            "SUM(total_amount) as revenue " +
            "FROM invoice " +
            "WHERE issue_date BETWEEN :startDate AND :endDate " +
            "GROUP BY EXTRACT(YEAR FROM issue_date), EXTRACT(MONTH FROM issue_date) " +
            "ORDER BY year, month", nativeQuery = true)
    List<Object[]> findMonthlyRevenue(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT EXTRACT(YEAR FROM issue_date) as year, " +
            "SUM(total_amount) as revenue " +
            "FROM invoice " +
            "WHERE issue_date BETWEEN :startDate AND :endDate " +
            "GROUP BY EXTRACT(YEAR FROM issue_date) " +
            "ORDER BY year", nativeQuery = true)
    List<Object[]> findYearlyRevenue(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    // Hotel-specific revenue queries
    @Query("SELECT i.issueDate, SUM(i.totalAmount) FROM Invoice i " +
            "JOIN i.stay s " +
            "JOIN s.reservation r " +
            "JOIN r.reservationRooms rr " +
            "JOIN rr.room rm " +
            "WHERE i.issueDate BETWEEN :startDate AND :endDate " +
            "AND rm.hotel.hotelId = :hotelId " +
            "GROUP BY i.issueDate " +
            "ORDER BY i.issueDate")
    List<Object[]> findDailyRevenueByHotel(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           @Param("hotelId") Long hotelId);

    @Query(value = "SELECT EXTRACT(YEAR FROM i.issue_date) as year, " +
            "EXTRACT(MONTH FROM i.issue_date) as month, " +
            "SUM(i.total_amount) as revenue " +
            "FROM invoice i " +
            "JOIN stay s ON i.stay_id = s.stay_id " +
            "JOIN reservation r ON s.reservation_id = r.reservation_id " +
            "JOIN reservation_room rr ON r.reservation_id = rr.reservation_id " +
            "JOIN room rm ON rr.room_id = rm.room_id " +
            "WHERE i.issue_date BETWEEN :startDate AND :endDate " +
            "AND rm.hotel_id = :hotelId " +
            "GROUP BY EXTRACT(YEAR FROM i.issue_date), EXTRACT(MONTH FROM i.issue_date) " +
            "ORDER BY year, month", nativeQuery = true)
    List<Object[]> findMonthlyRevenueByHotel(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate,
                                             @Param("hotelId") Long hotelId);

    @Query(value = "SELECT EXTRACT(YEAR FROM i.issue_date) as year, " +
            "SUM(i.total_amount) as revenue " +
            "FROM invoice i " +
            "JOIN stay s ON i.stay_id = s.stay_id " +
            "JOIN reservation r ON s.reservation_id = r.reservation_id " +
            "JOIN reservation_room rr ON r.reservation_id = rr.reservation_id " +
            "JOIN room rm ON rr.room_id = rm.room_id " +
            "WHERE i.issue_date BETWEEN :startDate AND :endDate " +
            "AND rm.hotel_id = :hotelId " +
            "GROUP BY EXTRACT(YEAR FROM i.issue_date) " +
            "ORDER BY year", nativeQuery = true)
    List<Object[]> findYearlyRevenueByHotel(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("hotelId") Long hotelId);

    // Total revenue for a period
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i " +
            "WHERE i.issueDate BETWEEN :startDate AND :endDate")
    Double findTotalRevenueByPeriod(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    // Total revenue for a specific hotel and period
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i " +
            "JOIN i.stay s " +
            "JOIN s.reservation r " +
            "JOIN r.reservationRooms rr " +
            "JOIN rr.room rm " +
            "WHERE i.issueDate BETWEEN :startDate AND :endDate " +
            "AND rm.hotel.hotelId = :hotelId")
    Double findTotalRevenueByHotelAndPeriod(@Param("hotelId") Long hotelId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    // Revenue by hotel for a period
    @Query("SELECT r.hotel.name, SUM(i.totalAmount) FROM Invoice i " +
            "JOIN i.stay s " +
            "JOIN s.reservation res " +
            "JOIN res.reservationRooms rr " +
            "JOIN rr.room r " +
            "WHERE i.issueDate BETWEEN :startDate AND :endDate " +
            "GROUP BY r.hotel.name")
    List<Object[]> findRevenueByHotel(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    // Get paid invoices only (for accurate revenue calculation)
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i " +
            "JOIN i.payments p " +
            "WHERE i.issueDate BETWEEN :startDate AND :endDate " +
            "AND p.status = 'COMPLETED'")
    Double findPaidRevenueByPeriod(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    // Get revenue by service type (from service charges)
    @Query("SELECT sc.serviceType, SUM(sc.amount) FROM ServiceCharge sc " +
            "JOIN sc.stay s " +
            "JOIN s.invoice i " +
            "WHERE i.issueDate BETWEEN :startDate AND :endDate " +
            "GROUP BY sc.serviceType")
    List<Object[]> findServiceRevenueByPeriod(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}