package com.example.demo.DTOs;

import com.example.demo.Entities.RoomStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RoomStatusDTO {
    private Long roomId;
    private String roomNumber;
    private RoomStatus status;
    private RoomTypeDTO roomType;
    private String hotelName;
    private LocalDate bookedUntil;

    // Constructors
    public RoomStatusDTO() {}

    public RoomStatusDTO(Long roomId, String roomNumber, RoomStatus status,
                         RoomTypeDTO roomType, String hotelName, LocalDate bookedUntil) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.status = status;
        this.roomType = roomType;
        this.hotelName = hotelName;
        this.bookedUntil = bookedUntil;
    }

    // Getters and Setters
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public RoomTypeDTO getRoomType() { return roomType; }
    public void setRoomType(RoomTypeDTO roomType) { this.roomType = roomType; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public LocalDate getBookedUntil() { return bookedUntil; }
    public void setBookedUntil(LocalDate bookedUntil) { this.bookedUntil = bookedUntil; }
}