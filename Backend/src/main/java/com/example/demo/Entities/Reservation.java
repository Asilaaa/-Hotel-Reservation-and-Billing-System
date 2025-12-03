package com.example.demo.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private BigDecimal totalAmount;
    private String specialRequests;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @ManyToOne
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @OneToMany(mappedBy = "reservation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<ReservationRoom> reservationRooms = new ArrayList<>();

    @OneToOne(mappedBy = "reservation")
    private Stay stay;

    // Add these fields if you need timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @AssertTrue(message = "Check-out date must be after check-in date")
    public boolean isCheckOutAfterCheckIn() {
        return checkOutDate == null || checkInDate == null || checkOutDate.isAfter(checkInDate);
    }

    // Getters
    public Long getReservationId() {
        return reservationId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Guest getGuest() {
        return guest;
    }

    public List<ReservationRoom> getReservationRooms() {
        return reservationRooms;
    }

    public Stay getStay() {
        return stay;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public void setReservationRooms(List<ReservationRoom> reservationRooms) {
        this.reservationRooms = reservationRooms;
    }

    public void setStay(Stay stay) {
        this.stay = stay;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void addReservationRoom(ReservationRoom reservationRoom) {
        reservationRooms.add(reservationRoom);
        reservationRoom.setReservation(this);
    }

    public void removeReservationRoom(ReservationRoom reservationRoom) {
        reservationRooms.remove(reservationRoom);
        reservationRoom.setReservation(null);
    }
}