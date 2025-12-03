package com.example.demo.DTOs;

import java.time.LocalDateTime;

public class ServiceChargeDTO {
    private Long chargeId;
    private String serviceType;
    private Double amount;
    private String description;
    private LocalDateTime chargedAt;

    // Constructors
    public ServiceChargeDTO() {}

    public ServiceChargeDTO(Long chargeId, String serviceType, Double amount,
                            String description, LocalDateTime chargedAt) {
        this.chargeId = chargeId;
        this.serviceType = serviceType;
        this.amount = amount;
        this.description = description;
        this.chargedAt = chargedAt;
    }

    // Getters and Setters
    public Long getChargeId() { return chargeId; }
    public void setChargeId(Long chargeId) { this.chargeId = chargeId; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getChargedAt() { return chargedAt; }
    public void setChargedAt(LocalDateTime chargedAt) { this.chargedAt = chargedAt; }
}