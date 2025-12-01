package com.example.demo.Entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "service_charge_stats")
public class ServiceChargeStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "service_type", length = 50)
    private String serviceType;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "count_used")
    private Integer countUsed = 0;

    public ServiceChargeStats() {
    }

    public ServiceChargeStats(Long id, Hotel hotel, LocalDate statDate, String serviceType, BigDecimal totalAmount, Integer countUsed) {
        this.id = id;
        this.hotel = hotel;
        this.statDate = statDate;
        this.serviceType = serviceType;
        this.totalAmount = totalAmount;
        this.countUsed = countUsed;
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
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getCountUsed() {
        return countUsed;
    }

    public void setCountUsed(Integer countUsed) {
        this.countUsed = countUsed;
    }

    @Override
    public String toString() {
        return "ServiceChargeStats{" +
                "id=" + id +
                ", hotel=" + hotel +
                ", statDate=" + statDate +
                ", serviceType='" + serviceType + '\'' +
                ", totalAmount=" + totalAmount +
                ", countUsed=" + countUsed +
                '}';
    }
}