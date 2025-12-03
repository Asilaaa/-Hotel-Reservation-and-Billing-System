package com.example.demo.Entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    private BigDecimal totalAmount;
    private BigDecimal roomCharges;
    private BigDecimal serviceCharges;
    private LocalDateTime issueDate;
    private String status;

    @OneToOne
    @JoinColumn(name = "stay_id")
    private Stay stay;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<Payment> payments;

    // Getters and Setters
    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getRoomCharges() {
        return roomCharges;
    }

    public void setRoomCharges(BigDecimal roomCharges) {
        this.roomCharges = roomCharges;
    }

    public BigDecimal getServiceCharges() {
        return serviceCharges;
    }

    public void setServiceCharges(BigDecimal serviceCharges) {
        this.serviceCharges = serviceCharges;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Stay getStay() {
        return stay;
    }

    public void setStay(Stay stay) {
        this.stay = stay;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}