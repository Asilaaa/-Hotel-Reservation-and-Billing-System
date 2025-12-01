package com.example.demo.DTOs;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OccupancyStatsDTO {
    private final Long statId;
    private final Long hotelId;
    private final String hotelName;
    private final String periodType;
    private final LocalDate periodDate;
    private final Integer totalRooms;
    private final Integer occupiedRooms;
    private final Double occupancyRate;
    private final LocalDateTime lastUpdated;

    public OccupancyStatsDTO(Long statId, Long hotelId, String hotelName, String periodType,
                             LocalDate periodDate, Integer totalRooms, Integer occupiedRooms,
                             Double occupancyRate, LocalDateTime lastUpdated) {
        this.statId = statId;
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.periodType = periodType;
        this.periodDate = periodDate;
        this.totalRooms = totalRooms;
        this.occupiedRooms = occupiedRooms;
        this.occupancyRate = occupancyRate;
        this.lastUpdated = lastUpdated;
    }

    // Getters only
    public Long getStatId() { return statId; }
    public Long getHotelId() { return hotelId; }
    public String getHotelName() { return hotelName; }
    public String getPeriodType() { return periodType; }
    public LocalDate getPeriodDate() { return periodDate; }
    public Integer getTotalRooms() { return totalRooms; }
    public Integer getOccupiedRooms() { return occupiedRooms; }
    public Double getOccupancyRate() { return occupancyRate; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
}