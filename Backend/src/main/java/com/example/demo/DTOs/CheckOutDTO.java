package com.example.demo.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class CheckOutDTO {
    private Long reservationId;  // Changed from stayId to reservationId

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actualCheckOut;

    // Constructors
    public CheckOutDTO() {}

    public CheckOutDTO(Long reservationId, LocalDateTime actualCheckOut) {
        this.reservationId = reservationId;
        this.actualCheckOut = actualCheckOut;
    }

    // Getters and Setters
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public LocalDateTime getActualCheckOut() { return actualCheckOut; }
    public void setActualCheckOut(LocalDateTime actualCheckOut) { this.actualCheckOut = actualCheckOut; }
}