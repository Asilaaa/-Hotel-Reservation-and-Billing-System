package com.example.demo.Entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "hotel_revenue_stats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hotel_id", "stat_period", "period_type"})
})
public class HotelRevenueStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Long statId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "stat_period", nullable = false)
    private LocalDate statPeriod;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false, length = 10)
    private PeriodType periodType;

    @Column(name = "room_revenue", precision = 12, scale = 2)
    private BigDecimal roomRevenue = BigDecimal.ZERO;

    @Column(name = "service_revenue", precision = 12, scale = 2)
    private BigDecimal serviceRevenue = BigDecimal.ZERO;

    @Column(name = "total_revenue", precision = 12, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "avg_daily_revenue", precision = 10, scale = 2)
    private BigDecimal avgDailyRevenue = BigDecimal.ZERO;

    @Column(name = "avg_room_rate", precision = 10, scale = 2)
    private BigDecimal avgRoomRate = BigDecimal.ZERO;

    @Column(name = "avg_service_per_stay", precision = 10, scale = 2)
    private BigDecimal avgServicePerStay = BigDecimal.ZERO;

    @Column(name = "total_stays")
    private Integer totalStays = 0;

    @Column(name = "total_rooms_booked")
    private Integer totalRoomsBooked = 0;

    @Column(name = "total_services")
    private Integer totalServices = 0;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    public HotelRevenueStats() {
        this.calculatedAt = LocalDateTime.now();
    }

    public HotelRevenueStats(Hotel hotel, LocalDate statPeriod, PeriodType periodType) {
        this.hotel = hotel;
        this.statPeriod = statPeriod;
        this.periodType = periodType;
        this.calculatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getStatId() { return statId; }
    public void setStatId(Long statId) { this.statId = statId; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public LocalDate getStatPeriod() { return statPeriod; }
    public void setStatPeriod(LocalDate statPeriod) { this.statPeriod = statPeriod; }

    public PeriodType getPeriodType() { return periodType; }
    public void setPeriodType(PeriodType periodType) { this.periodType = periodType; }

    public BigDecimal getRoomRevenue() { return roomRevenue; }
    public void setRoomRevenue(BigDecimal roomRevenue) { this.roomRevenue = roomRevenue; }

    public BigDecimal getServiceRevenue() { return serviceRevenue; }
    public void setServiceRevenue(BigDecimal serviceRevenue) { this.serviceRevenue = serviceRevenue; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getAvgDailyRevenue() { return avgDailyRevenue; }
    public void setAvgDailyRevenue(BigDecimal avgDailyRevenue) { this.avgDailyRevenue = avgDailyRevenue; }

    public BigDecimal getAvgRoomRate() { return avgRoomRate; }
    public void setAvgRoomRate(BigDecimal avgRoomRate) { this.avgRoomRate = avgRoomRate; }

    public BigDecimal getAvgServicePerStay() { return avgServicePerStay; }
    public void setAvgServicePerStay(BigDecimal avgServicePerStay) { this.avgServicePerStay = avgServicePerStay; }

    public Integer getTotalStays() { return totalStays; }
    public void setTotalStays(Integer totalStays) { this.totalStays = totalStays; }

    public Integer getTotalRoomsBooked() { return totalRoomsBooked; }
    public void setTotalRoomsBooked(Integer totalRoomsBooked) { this.totalRoomsBooked = totalRoomsBooked; }

    public Integer getTotalServices() { return totalServices; }
    public void setTotalServices(Integer totalServices) { this.totalServices = totalServices; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelRevenueStats that = (HotelRevenueStats) o;
        return Objects.equals(statId, that.statId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statId);
    }

    @Override
    public String toString() {
        return "HotelRevenueStats{" +
                "statId=" + statId +
                ", hotel=" + hotel.getHotelId() +
                ", statPeriod=" + statPeriod +
                ", periodType=" + periodType +
                ", roomRevenue=" + roomRevenue +
                ", serviceRevenue=" + serviceRevenue +
                ", totalRevenue=" + totalRevenue +
                '}';
    }
}