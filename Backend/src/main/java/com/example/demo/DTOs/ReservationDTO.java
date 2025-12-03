package com.example.demo.DTOs;

import com.example.demo.Entities.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationDTO {
    private Long reservationId;
    private GuestDTO guest;
    private List<RoomDTO> rooms;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private ReservationStatus status;
    private String specialRequests;
    private LocalDateTime createdAt;

    // Constructors
    public ReservationDTO() {}

    public ReservationDTO(Long reservationId, GuestDTO guest, List<RoomDTO> rooms,
                          LocalDate checkInDate, LocalDate checkOutDate,
                          ReservationStatus status, String specialRequests,
                          LocalDateTime createdAt) {
        this.reservationId = reservationId;
        this.guest = guest;
        this.rooms = rooms;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.specialRequests = specialRequests;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public GuestDTO getGuest() { return guest; }
    public void setGuest(GuestDTO guest) { this.guest = guest; }

    public List<RoomDTO> getRooms() { return rooms; }
    public void setRooms(List<RoomDTO> rooms) { this.rooms = rooms; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}