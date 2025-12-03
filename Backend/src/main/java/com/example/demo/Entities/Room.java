package com.example.demo.Entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(unique = true)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    private BigDecimal ratePerNight;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @OneToMany(mappedBy = "room")
    private List<ReservationRoom> reservationRooms = new ArrayList<>();

    // Getters
    public Long getRoomId() {
        return roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public List<ReservationRoom> getReservationRooms() {
        return reservationRooms;
    }

    // Setters
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setReservationRooms(List<ReservationRoom> reservationRooms) {
        this.reservationRooms = reservationRooms;
    }
}