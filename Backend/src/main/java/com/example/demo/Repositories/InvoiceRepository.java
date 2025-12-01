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

    List<Invoice> findAllByIssueDate(LocalDate issueDate);

    // =================== DAILY =======================

    @Query("SELECT i.issueDate, SUM(i.totalAmount) FROM Invoice i " +
            "WHERE i.issueDate BETWEEN :startDate AND :endDate " +
            "GROUP BY i.issueDate ORDER BY i.issueDate")
    List<Object[]> findDailyRevenue(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    @Query("SELECT i.issueDate, SUM(i.totalAmount) FROM Invoice i " +
            "JOIN i.stay s " +
            "JOIN s.reservation r " +
            "JOIN r.reservationRooms rr " +
            "JOIN rr.room rm " +
            "WHERE i.issueDate BETWEEN :startDate AND :endDate " +
            "AND rm.hotel.hotelId = :hotelId " +
            "GROUP BY i.issueDate ORDER BY i.issueDate")
    List<Object[]> findDailyRevenueByHotel(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           @Param("hotelId") Long hotelId);


    // =================== MONTHLY =======================

    @Query(value = "SELECT EXTRACT(YEAR FROM issue_date), EXTRACT(MONTH FROM issue_date), " +
            "SUM(total_amount) FROM invoice " +
            "WHERE issue_date BETWEEN :startDate AND :endDate " +
            "GROUP BY EXTRACT(YEAR FROM issue_date), EXTRACT(MONTH FROM issue_date) " +
            "ORDER BY 1, 2", nativeQuery = true)
    List<Object[]> findMonthlyRevenue(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Query(value =
            "SELECT EXTRACT(YEAR FROM i.issue_date), EXTRACT(MONTH FROM i.issue_date), SUM(i.total_amount) " +
                    "FROM invoice i " +
                    "JOIN stay s ON i.stay_id = s.stay_id " +
                    "JOIN reservation r ON s.reservation_id = r.reservation_id " +
                    "JOIN reservation_room rr ON r.reservation_id = rr.reservation_id " +
                    "JOIN room rm ON rr.room_id = rm.room_id " +
                    "WHERE i.issue_date BETWEEN :startDate AND :endDate " +
                    "AND rm.hotel_id = :hotelId " +
                    "GROUP BY EXTRACT(YEAR FROM i.issue_date), EXTRACT(MONTH FROM i.issue_date) " +
                    "ORDER BY 1, 2",
            nativeQuery = true)
    List<Object[]> findMonthlyRevenueByHotel(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate,
                                             @Param("hotelId") Long hotelId);


    // =================== YEARLY =======================

    @Query(value = "SELECT EXTRACT(YEAR FROM issue_date), SUM(total_amount) " +
            "FROM invoice " +
            "WHERE issue_date BETWEEN :startDate AND :endDate " +
            "GROUP BY EXTRACT(YEAR FROM issue_date) ORDER BY 1",
            nativeQuery = true)
    List<Object[]> findYearlyRevenue(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    @Query(value =
            "SELECT EXTRACT(YEAR FROM i.issue_date), SUM(i.total_amount) " +
                    "FROM invoice i " +
                    "JOIN stay s ON i.stay_id = s.stay_id " +
                    "JOIN reservation r ON s.reservation_id = r.reservation_id " +
                    "JOIN reservation_room rr ON r.reservation_id = rr.reservation_id " +
                    "JOIN room rm ON rr.room_id = rm.room_id " +
                    "WHERE i.issue_date BETWEEN :startDate AND :endDate " +
                    "AND rm.hotel_id = :hotelId " +
                    "GROUP BY EXTRACT(YEAR FROM i.issue_date) ORDER BY 1",
            nativeQuery = true)
    List<Object[]> findYearlyRevenueByHotel(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("hotelId") Long hotelId);


    // =================== TOTAL =======================

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i " +
            "WHERE i.issueDate BETWEEN :startDate AND :endDate")
    Double findTotalRevenueByPeriod(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
}
