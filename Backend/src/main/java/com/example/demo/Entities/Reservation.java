package com.example.demo.Entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;

import java.time.LocalDate;
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

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @ManyToOne
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<ReservationRoom> reservationRooms = new ArrayList<>();

    @OneToOne(mappedBy = "reservation")
    private Stay stay;

    @AssertTrue(message = "Check-out date must be after check-in date")
    public boolean isCheckOutAfterCheckIn() {
        return checkOutDate.isAfter(checkInDate);
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Guest getGuest() {
        return guest;
    }
}
