package com.example.demo.DTOs;

import java.time.LocalDate;
import java.util.List;

public class CreateReservationDTO {
    private Long guestId;
    private List<Long> roomIds;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String specialRequests;

    // Constructors
    public CreateReservationDTO() {}

    public CreateReservationDTO(Long guestId, List<Long> roomIds, LocalDate checkInDate,
                                LocalDate checkOutDate, String specialRequests) {
        this.guestId = guestId;
        this.roomIds = roomIds;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.specialRequests = specialRequests;
    }

    // Getters and Setters
    public Long getGuestId() { return guestId; }
    public void setGuestId(Long guestId) { this.guestId = guestId; }

    public List<Long> getRoomIds() { return roomIds; }
    public void setRoomIds(List<Long> roomIds) { this.roomIds = roomIds; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
}