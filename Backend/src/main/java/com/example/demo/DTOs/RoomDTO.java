package com.example.demo.DTOs;

import com.example.demo.Entities.RoomStatus;
import java.math.BigDecimal;

public class RoomDTO {
    private Long roomId;
    private String roomNumber;
    private RoomStatus status;
    private RoomTypeDTO roomType;
    private BigDecimal ratePerNight;

    // Constructors
    public RoomDTO() {}

    public RoomDTO(Long roomId, String roomNumber, RoomStatus status,
                   RoomTypeDTO roomType, BigDecimal ratePerNight) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.status = status;
        this.roomType = roomType;
        this.ratePerNight = ratePerNight;
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

    public BigDecimal getRatePerNight() { return ratePerNight; }
    public void setRatePerNight(BigDecimal ratePerNight) { this.ratePerNight = ratePerNight; }
}