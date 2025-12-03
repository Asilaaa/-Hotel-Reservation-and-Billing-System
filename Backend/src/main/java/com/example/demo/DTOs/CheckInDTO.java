package com.example.demo.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class CheckInDTO {
    private Long reservationId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actualCheckIn;

    // Constructors
    public CheckInDTO() {}

    public CheckInDTO(Long reservationId, LocalDateTime actualCheckIn) {
        this.reservationId = reservationId;
        this.actualCheckIn = actualCheckIn;
    }

    // Getters and Setters
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public LocalDateTime getActualCheckIn() { return actualCheckIn; }
    public void setActualCheckIn(LocalDateTime actualCheckIn) { this.actualCheckIn = actualCheckIn; }
}