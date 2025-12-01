package com.example.demo.Repositories;

import com.example.demo.Entities.Reservation;
import com.example.demo.Entities.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Find all reservations by guest ID
    List<Reservation> findAllByGuest_GuestId(Long guestId);

    // Find all reservations by status
    List<Reservation> findAllByStatus(ReservationStatus status);
}
