package com.example.demo.Repositories;

import com.example.demo.Entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    Guest findByIdNumber(String idNumber);
    Guest findByEmail(String email);

    // Count guests by hotel
    @Query("SELECT COUNT(DISTINCT r.guest) FROM Reservation r " +
            "JOIN r.reservationRooms rr " +
            "JOIN rr.room rm " +
            "WHERE rm.hotel.hotelId = :hotelId")
    long countByHotelId(@Param("hotelId") Long hotelId);

    // Average loyalty points for all guests
    @Query("SELECT AVG(g.loyaltyPoints) FROM Guest g")
    Double findAverageLoyaltyPoints();

    // Average loyalty points for guests of a specific hotel
    @Query("SELECT AVG(g.loyaltyPoints) FROM Guest g " +
            "JOIN Reservation r ON g.guestId = r.guest.guestId " +
            "JOIN r.reservationRooms rr " +
            "JOIN rr.room rm " +
            "WHERE rm.hotel.hotelId = :hotelId")
    Double findAverageLoyaltyPointsByHotelId(@Param("hotelId") Long hotelId);

    // Count VIP guests (loyalty points > 200) for all hotels
    @Query("SELECT COUNT(g) FROM Guest g WHERE g.loyaltyPoints > 200")
    long countVIPGuests();

    // Count VIP guests for a specific hotel
    @Query("SELECT COUNT(DISTINCT g) FROM Guest g " +
            "JOIN Reservation r ON g.guestId = r.guest.guestId " +
            "JOIN r.reservationRooms rr " +
            "JOIN rr.room rm " +
            "WHERE rm.hotel.hotelId = :hotelId AND g.loyaltyPoints > 200")
    long countVIPGuestsByHotelId(@Param("hotelId") Long hotelId);
}