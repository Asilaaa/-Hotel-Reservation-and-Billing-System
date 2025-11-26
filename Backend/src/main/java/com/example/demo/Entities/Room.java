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

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @OneToMany(mappedBy = "room")
    private List<ReservationRoom> reservationRooms = new ArrayList<>();


}
