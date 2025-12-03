package com.example.demo.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stay")
public class Stay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stayId;

    private LocalDateTime actualCheckIn;
    private LocalDateTime actualCheckOut;
    private String additionalRequests;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @OneToMany(mappedBy = "stay", cascade = CascadeType.ALL)
    private List<ServiceCharge> serviceCharges = new ArrayList<>();

    @OneToOne(mappedBy = "stay", cascade = CascadeType.ALL)
    private Invoice invoice;

    // Getters and Setters
    public Long getStayId() {
        return stayId;
    }

    public void setStayId(Long stayId) {
        this.stayId = stayId;
    }

    public LocalDateTime getActualCheckIn() {
        return actualCheckIn;
    }

    public void setActualCheckIn(LocalDateTime actualCheckIn) {
        this.actualCheckIn = actualCheckIn;
    }

    public LocalDateTime getActualCheckOut() {
        return actualCheckOut;
    }

    public void setActualCheckOut(LocalDateTime actualCheckOut) {
        this.actualCheckOut = actualCheckOut;
    }

    public String getAdditionalRequests() {
        return additionalRequests;
    }

    public void setAdditionalRequests(String additionalRequests) {
        this.additionalRequests = additionalRequests;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public List<ServiceCharge> getServiceCharges() {
        return serviceCharges;
    }

    public void setServiceCharges(List<ServiceCharge> serviceCharges) {
        this.serviceCharges = serviceCharges;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (actualCheckOut != null && actualCheckIn != null) {
            if (actualCheckOut.isBefore(actualCheckIn)) {
                throw new IllegalArgumentException("Check-out time cannot be before check-in time");
            }
        }
    }
}