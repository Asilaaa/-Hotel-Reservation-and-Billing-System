package com.example.demo.Entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "revenue_stats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hotel_id", "stat_date", "period_type"})
})
public class RevenueStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "room_revenue", precision = 10, scale = 2)
    private BigDecimal roomRevenue = BigDecimal.ZERO;

    @Column(name = "service_revenue", precision = 10, scale = 2)
    private BigDecimal serviceRevenue = BigDecimal.ZERO;

    @Column(name = "total_revenue", precision = 10, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    // New fields for period handling
    @Column(name = "period_type", length = 10)
    private String periodType = "daily";

    @Column(name = "year")
    private Integer year;

    @Column(name = "month")
    private Integer month;

    @Column(name = "day")
    private Integer day;

    public RevenueStats() {
    }

    public RevenueStats(Long id, Hotel hotel, LocalDate statDate, BigDecimal roomRevenue,
                        BigDecimal serviceRevenue, BigDecimal totalRevenue, String periodType) {
        this.id = id;
        this.hotel = hotel;
        this.statDate = statDate;
        this.roomRevenue = roomRevenue;
        this.serviceRevenue = serviceRevenue;
        this.totalRevenue = totalRevenue;
        this.periodType = periodType;
        // Auto-populate year, month, day from statDate
        if (statDate != null) {
            this.year = statDate.getYear();
            this.month = statDate.getMonthValue();
            this.day = statDate.getDayOfMonth();
        }
    }

    // Constructor for aggregated data without hotel (for "all hotels" queries)
    public RevenueStats(Long hotelId, LocalDate statDate, BigDecimal roomRevenue,
                        BigDecimal serviceRevenue, BigDecimal totalRevenue, String periodType) {
        this.hotel = new Hotel();
        this.hotel.setHotelId(hotelId);
        this.statDate = statDate;
        this.roomRevenue = roomRevenue;
        this.serviceRevenue = serviceRevenue;
        this.totalRevenue = totalRevenue;
        this.periodType = periodType;
        if (statDate != null) {
            this.year = statDate.getYear();
            this.month = statDate.getMonthValue();
            this.day = statDate.getDayOfMonth();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public LocalDate getStatDate() {
        return statDate;
    }

    public void setStatDate(LocalDate statDate) {
        this.statDate = statDate;
        // Auto-populate year, month, day when statDate is set
        if (statDate != null) {
            this.year = statDate.getYear();
            this.month = statDate.getMonthValue();
            this.day = statDate.getDayOfMonth();
        }
    }

    public BigDecimal getRoomRevenue() {
        return roomRevenue;
    }

    public void setRoomRevenue(BigDecimal roomRevenue) {
        this.roomRevenue = roomRevenue;
    }

    public BigDecimal getServiceRevenue() {
        return serviceRevenue;
    }

    public void setServiceRevenue(BigDecimal serviceRevenue) {
        this.serviceRevenue = serviceRevenue;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    @PrePersist
    @PreUpdate
    private void populateDateComponents() {
        // Ensure year, month, day are populated from statDate
        if (statDate != null) {
            this.year = statDate.getYear();
            this.month = statDate.getMonthValue();
            this.day = statDate.getDayOfMonth();
        }
    }

    @Override
    public String toString() {
        return "RevenueStats{" +
                "id=" + id +
                ", hotel=" + (hotel != null ? hotel.getHotelId() : "null") +
                ", statDate=" + statDate +
                ", roomRevenue=" + roomRevenue +
                ", serviceRevenue=" + serviceRevenue +
                ", totalRevenue=" + totalRevenue +
                ", periodType='" + periodType + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                '}';
    }
}