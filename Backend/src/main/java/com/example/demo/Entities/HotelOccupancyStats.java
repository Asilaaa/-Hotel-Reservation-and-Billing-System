package com.example.demo.Entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_occupancy_stats")
public class HotelOccupancyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Long statId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "period_type", nullable = false)
    private String periodType;

    @Column(name = "period_date", nullable = false)
    private LocalDate periodDate;

    @Column(name = "total_rooms", nullable = false)
    private Integer totalRooms;

    @Column(name = "occupied_rooms", nullable = false)
    private Integer occupiedRooms;

    @Column(name = "occupancy_rate", nullable = false)
    private Double occupancyRate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // Constructors
    public HotelOccupancyStats() {
        this.lastUpdated = LocalDateTime.now();
    }

    public HotelOccupancyStats(Hotel hotel, String periodType, LocalDate periodDate,
                               Integer totalRooms, Integer occupiedRooms, Double occupancyRate) {
        this();
        this.hotel = hotel;
        this.periodType = periodType;
        this.periodDate = periodDate;
        this.totalRooms = totalRooms;
        this.occupiedRooms = occupiedRooms;
        this.occupancyRate = occupancyRate;
    }

    // Getters and Setters
    public Long getStatId() { return statId; }
    public void setStatId(Long statId) { this.statId = statId; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public String getPeriodType() { return periodType; }
    public void setPeriodType(String periodType) { this.periodType = periodType; }

    public LocalDate getPeriodDate() { return periodDate; }
    public void setPeriodDate(LocalDate periodDate) { this.periodDate = periodDate; }

    public Integer getTotalRooms() { return totalRooms; }
    public void setTotalRooms(Integer totalRooms) { this.totalRooms = totalRooms; }

    public Integer getOccupiedRooms() { return occupiedRooms; }
    public void setOccupiedRooms(Integer occupiedRooms) { this.occupiedRooms = occupiedRooms; }

    public Double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(Double occupancyRate) { this.occupancyRate = occupancyRate; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}