package com.example.demo.Repositories;

import com.example.demo.Entities.Stay;
import com.example.demo.Entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StayRepository extends JpaRepository<Stay, Long> {
    Stay findByReservation(Reservation reservation);
    List<Stay> findByActualCheckOutIsNull(); // Current in-house guests
}