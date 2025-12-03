package com.example.demo.Entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_charge")
public class ServiceCharge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chargeId;

    private String serviceType;
    private BigDecimal amount;
    private String description;
    private LocalDateTime chargeDate;

    @ManyToOne
    @JoinColumn(name = "stay_id")
    private Stay stay;

    // Getters and Setters
    public Long getChargeId() {
        return chargeId;
    }

    public void setChargeId(Long chargeId) {
        this.chargeId = chargeId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getChargeDate() {
        return chargeDate;
    }

    public void setChargeDate(LocalDateTime chargeDate) {
        this.chargeDate = chargeDate;
    }

    public Stay getStay() {
        return stay;
    }

    public void setStay(Stay stay) {
        this.stay = stay;
    }
}