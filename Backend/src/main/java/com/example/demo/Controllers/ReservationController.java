package com.example.demo.Controllers;

import com.example.demo.DTOs.*;
import com.example.demo.Entities.Reservation;
import com.example.demo.Entities.RoomStatus;
import com.example.demo.Repositories.ReservationRepository;
import com.example.demo.Services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody CreateReservationDTO createReservationDTO) {
        ReservationDTO reservation = reservationService.createReservation(createReservationDTO);
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> updateReservation(
            @PathVariable Long reservationId,
            @RequestBody UpdateReservationDTO updateReservationDTO) {
        ReservationDTO reservation = reservationService.updateReservation(reservationId, updateReservationDTO);
        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/guest/{guestId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByGuest(@PathVariable Long guestId) {
        List<ReservationDTO> reservations = reservationService.getReservationsByGuest(guestId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long reservationId) {
        ReservationDTO reservation = reservationService.getReservationById(reservationId);
        return ResponseEntity.ok(reservation);
    }

    @PostMapping("/check-in")
    public ResponseEntity<StayDTO> checkIn(@RequestBody CheckInDTO checkInDTO) {
        StayDTO stay = reservationService.checkIn(checkInDTO);
        return ResponseEntity.ok(stay);
    }

    @PostMapping("/check-out")
    public ResponseEntity<StayDTO> checkOut(@RequestBody CheckOutDTO checkOutDTO) {
        StayDTO stay = reservationService.checkOut(checkOutDTO);
        return ResponseEntity.ok(stay);
    }

    @GetMapping("/rooms/status")
    public ResponseEntity<List<RoomStatusDTO>> getAllRoomStatuses(
            @RequestParam(required = false) Long hotelId) {
        List<RoomStatusDTO> roomStatuses = reservationService.getAllRoomStatuses(hotelId);
        return ResponseEntity.ok(roomStatuses);
    }

    @GetMapping("/rooms/status/{status}")
    public ResponseEntity<List<RoomStatusDTO>> getRoomsByStatus(
            @PathVariable RoomStatus status,
            @RequestParam(required = false) Long hotelId) {
        List<RoomStatusDTO> roomStatuses = reservationService.getRoomsByStatus(hotelId, status);
        return ResponseEntity.ok(roomStatuses);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }
}