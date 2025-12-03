package com.example.demo.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@JsonDeserialize(builder = RevenueStatsDTO.Builder.class)
public final class RevenueStatsDTO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate periodDate;

    private final String hotelName;
    private final BigDecimal roomRevenue;
    private final BigDecimal serviceRevenue;
    private final BigDecimal totalRevenue;
    private final BigDecimal avgDailyRevenue;
    private final BigDecimal avgRoomRate;
    private final BigDecimal avgServicePerStay;
    private final Integer totalStays;
    private final Integer totalRoomsBooked;
    private final Integer totalServices;

    private RevenueStatsDTO(Builder builder) {
        this.periodDate = builder.periodDate;
        this.hotelName = builder.hotelName;
        this.roomRevenue = builder.roomRevenue;
        this.serviceRevenue = builder.serviceRevenue;
        this.totalRevenue = builder.totalRevenue;
        this.avgDailyRevenue = builder.avgDailyRevenue;
        this.avgRoomRate = builder.avgRoomRate;
        this.avgServicePerStay = builder.avgServicePerStay;
        this.totalStays = builder.totalStays;
        this.totalRoomsBooked = builder.totalRoomsBooked;
        this.totalServices = builder.totalServices;
    }

    public LocalDate getPeriodDate() { return periodDate; }
    public String getHotelName() { return hotelName; }
    public BigDecimal getRoomRevenue() { return roomRevenue; }
    public BigDecimal getServiceRevenue() { return serviceRevenue; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public BigDecimal getAvgDailyRevenue() { return avgDailyRevenue; }
    public BigDecimal getAvgRoomRate() { return avgRoomRate; }
    public BigDecimal getAvgServicePerStay() { return avgServicePerStay; }
    public Integer getTotalStays() { return totalStays; }
    public Integer getTotalRoomsBooked() { return totalRoomsBooked; }
    public Integer getTotalServices() { return totalServices; }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private LocalDate periodDate;
        private String hotelName;
        private BigDecimal roomRevenue = BigDecimal.ZERO;
        private BigDecimal serviceRevenue = BigDecimal.ZERO;
        private BigDecimal totalRevenue = BigDecimal.ZERO;
        private BigDecimal avgDailyRevenue = BigDecimal.ZERO;
        private BigDecimal avgRoomRate = BigDecimal.ZERO;
        private BigDecimal avgServicePerStay = BigDecimal.ZERO;
        private Integer totalStays = 0;
        private Integer totalRoomsBooked = 0;
        private Integer totalServices = 0;

        private Builder() {}

        public Builder periodDate(LocalDate periodDate) {
            this.periodDate = periodDate;
            return this;
        }

        public Builder hotelName(String hotelName) {
            this.hotelName = hotelName;
            return this;
        }

        public Builder roomRevenue(BigDecimal roomRevenue) {
            this.roomRevenue = roomRevenue != null ? roomRevenue : BigDecimal.ZERO;
            return this;
        }

        public Builder serviceRevenue(BigDecimal serviceRevenue) {
            this.serviceRevenue = serviceRevenue != null ? serviceRevenue : BigDecimal.ZERO;
            return this;
        }

        public Builder totalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
            return this;
        }

        public Builder avgDailyRevenue(BigDecimal avgDailyRevenue) {
            this.avgDailyRevenue = avgDailyRevenue != null ? avgDailyRevenue : BigDecimal.ZERO;
            return this;
        }

        public Builder avgRoomRate(BigDecimal avgRoomRate) {
            this.avgRoomRate = avgRoomRate != null ? avgRoomRate : BigDecimal.ZERO;
            return this;
        }

        public Builder avgServicePerStay(BigDecimal avgServicePerStay) {
            this.avgServicePerStay = avgServicePerStay != null ? avgServicePerStay : BigDecimal.ZERO;
            return this;
        }

        public Builder totalStays(Integer totalStays) {
            this.totalStays = totalStays != null ? totalStays : 0;
            return this;
        }

        public Builder totalRoomsBooked(Integer totalRoomsBooked) {
            this.totalRoomsBooked = totalRoomsBooked != null ? totalRoomsBooked : 0;
            return this;
        }

        public Builder totalServices(Integer totalServices) {
            this.totalServices = totalServices != null ? totalServices : 0;
            return this;
        }

        public RevenueStatsDTO build() {
            Objects.requireNonNull(periodDate, "periodDate must not be null");
            Objects.requireNonNull(hotelName, "hotelName must not be null");
            return new RevenueStatsDTO(this);
        }
    }

    public static RevenueStatsDTO fromEntity(
            LocalDate periodDate,
            String hotelName,
            BigDecimal roomRevenue,
            BigDecimal serviceRevenue,
            BigDecimal totalRevenue,
            BigDecimal avgDailyRevenue,
            BigDecimal avgRoomRate,
            BigDecimal avgServicePerStay,
            Integer totalStays,
            Integer totalRoomsBooked,
            Integer totalServices) {

        return RevenueStatsDTO.builder()
                .periodDate(periodDate)
                .hotelName(hotelName)
                .roomRevenue(roomRevenue)
                .serviceRevenue(serviceRevenue)
                .totalRevenue(totalRevenue)
                .avgDailyRevenue(avgDailyRevenue)
                .avgRoomRate(avgRoomRate)
                .avgServicePerStay(avgServicePerStay)
                .totalStays(totalStays)
                .totalRoomsBooked(totalRoomsBooked)
                .totalServices(totalServices)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RevenueStatsDTO that = (RevenueStatsDTO) o;
        return Objects.equals(periodDate, that.periodDate) &&
                Objects.equals(hotelName, that.hotelName) &&
                Objects.equals(roomRevenue, that.roomRevenue) &&
                Objects.equals(serviceRevenue, that.serviceRevenue) &&
                Objects.equals(totalRevenue, that.totalRevenue) &&
                Objects.equals(avgDailyRevenue, that.avgDailyRevenue) &&
                Objects.equals(avgRoomRate, that.avgRoomRate) &&
                Objects.equals(avgServicePerStay, that.avgServicePerStay) &&
                Objects.equals(totalStays, that.totalStays) &&
                Objects.equals(totalRoomsBooked, that.totalRoomsBooked) &&
                Objects.equals(totalServices, that.totalServices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodDate, hotelName, roomRevenue, serviceRevenue, totalRevenue,
                avgDailyRevenue, avgRoomRate, avgServicePerStay, totalStays, totalRoomsBooked, totalServices);
    }

    @Override
    public String toString() {
        return "RevenueStatsDTO{" +
                "periodDate=" + periodDate +
                ", hotelName='" + hotelName + '\'' +
                ", roomRevenue=" + roomRevenue +
                ", serviceRevenue=" + serviceRevenue +
                ", totalRevenue=" + totalRevenue +
                '}';
    }
}