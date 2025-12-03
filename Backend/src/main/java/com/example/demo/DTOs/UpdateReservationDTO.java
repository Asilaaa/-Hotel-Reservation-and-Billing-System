package com.example.demo.DTOs;

import java.time.LocalDate;
import java.util.List;

public class UpdateReservationDTO {
    private LocalDate newCheckInDate;
    private LocalDate newCheckOutDate;
    private List<Long> newRoomIds;
    private String specialRequests;

    // Constructors
    public UpdateReservationDTO() {}

    public UpdateReservationDTO(LocalDate newCheckInDate, LocalDate newCheckOutDate,
                                List<Long> newRoomIds, String specialRequests) {
        this.newCheckInDate = newCheckInDate;
        this.newCheckOutDate = newCheckOutDate;
        this.newRoomIds = newRoomIds;
        this.specialRequests = specialRequests;
    }

    // Getters and Setters
    public LocalDate getNewCheckInDate() { return newCheckInDate; }
    public void setNewCheckInDate(LocalDate newCheckInDate) { this.newCheckInDate = newCheckInDate; }

    public LocalDate getNewCheckOutDate() { return newCheckOutDate; }
    public void setNewCheckOutDate(LocalDate newCheckOutDate) { this.newCheckOutDate = newCheckOutDate; }

    public List<Long> getNewRoomIds() { return newRoomIds; }
    public void setNewRoomIds(List<Long> newRoomIds) { this.newRoomIds = newRoomIds; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
}