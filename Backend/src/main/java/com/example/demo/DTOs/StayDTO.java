package com.example.demo.DTOs;

import com.example.demo.Entities.ServiceCharge;
import java.time.LocalDateTime;
import java.util.List;

public class StayDTO {
    private Long stayId;
    private ReservationDTO reservation;
    private LocalDateTime actualCheckIn;
    private LocalDateTime actualCheckOut;
    private List<ServiceChargeDTO> serviceCharges;

    // Constructors
    public StayDTO() {}

    public StayDTO(Long stayId, ReservationDTO reservation, LocalDateTime actualCheckIn,
                   LocalDateTime actualCheckOut, List<ServiceChargeDTO> serviceCharges) {
        this.stayId = stayId;
        this.reservation = reservation;
        this.actualCheckIn = actualCheckIn;
        this.actualCheckOut = actualCheckOut;
        this.serviceCharges = serviceCharges;
    }

    // Getters and Setters
    public Long getStayId() { return stayId; }
    public void setStayId(Long stayId) { this.stayId = stayId; }

    public ReservationDTO getReservation() { return reservation; }
    public void setReservation(ReservationDTO reservation) { this.reservation = reservation; }

    public LocalDateTime getActualCheckIn() { return actualCheckIn; }
    public void setActualCheckIn(LocalDateTime actualCheckIn) { this.actualCheckIn = actualCheckIn; }

    public LocalDateTime getActualCheckOut() { return actualCheckOut; }
    public void setActualCheckOut(LocalDateTime actualCheckOut) { this.actualCheckOut = actualCheckOut; }

    public List<ServiceChargeDTO> getServiceCharges() { return serviceCharges; }
    public void setServiceCharges(List<ServiceChargeDTO> serviceCharges) { this.serviceCharges = serviceCharges; }
}