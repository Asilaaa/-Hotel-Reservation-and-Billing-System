package com.example.demo.Repositories;

import com.example.demo.Entities.ServiceCharge;
import com.example.demo.Entities.Stay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceChargeRepository extends JpaRepository<ServiceCharge, Long> {
    List<ServiceCharge> findByStay(Stay stay);
    List<ServiceCharge> findByServiceType(String serviceType);

    // Find service charges by hotel
    @Query("SELECT sc FROM ServiceCharge sc " +
            "JOIN sc.stay s " +
            "JOIN s.reservation r " +
            "JOIN r.reservationRooms rr " +
            "JOIN rr.room rm " +
            "WHERE rm.hotel.id = :hotelId")
    List<ServiceCharge> findByHotelId(@Param("hotelId") Long hotelId);

    // Get service revenue by hotel
    @Query("SELECT sc.serviceType, SUM(sc.amount) FROM ServiceCharge sc " +
            "JOIN sc.stay s " +
            "JOIN s.reservation r " +
            "JOIN r.reservationRooms rr " +
            "JOIN rr.room rm " +
            "WHERE rm.hotel.id = :hotelId " +
            "GROUP BY sc.serviceType")
    List<Object[]> findServiceRevenueByHotelId(@Param("hotelId") Long hotelId);
}