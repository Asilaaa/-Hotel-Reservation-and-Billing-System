package com.example.demo.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "reservation_room")
public class ReservationRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // Getters
    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Room getRoom() {
        return room;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}