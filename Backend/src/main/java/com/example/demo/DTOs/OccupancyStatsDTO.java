package com.example.demo.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class OccupancyStatsDTO {
    private Long statId;
    private Long hotelId;
    private String hotelName;
    private String periodType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate periodDate;

    private Integer totalRooms;
    private Integer occupiedRooms;
    private Double occupancyRate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastUpdated;

    // Constructor with all fields
    public OccupancyStatsDTO(Long statId, Long hotelId, String hotelName, String periodType,
                             LocalDate periodDate, Integer totalRooms, Integer occupiedRooms,
                             Double occupancyRate, LocalDate lastUpdated) {
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

    // Getters and setters
    public Long getStatId() { return statId; }
    public void setStatId(Long statId) { this.statId = statId; }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

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

    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }
}