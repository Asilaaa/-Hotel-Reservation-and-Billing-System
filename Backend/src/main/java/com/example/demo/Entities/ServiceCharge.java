package com.example.demo.Entities;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "service_charge")
public class ServiceCharge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chargeId;

    private String serviceType;
    private BigDecimal amount;
    private String description;

    @ManyToOne
    @JoinColumn(name = "stay_id")
    private Stay stay;

    public String getServiceType() {
        return serviceType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Stay getStay() {
        return stay;
    }
}
