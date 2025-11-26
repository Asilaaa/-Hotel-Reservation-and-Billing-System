package com.example.demo.Entities;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    private LocalDate issueDate;
    private BigDecimal totalAmount;

    @OneToOne
    @JoinColumn(name = "stay_id")
    private Stay stay;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Stay getStay() {
        return stay;
    }
}
