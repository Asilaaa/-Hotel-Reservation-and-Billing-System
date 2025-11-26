package com.example.demo.Controllers;

import com.example.demo.Services.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:3000")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/room-types")
    public List<Map<String, Object>> getRoomTypeDistribution(
            @RequestParam(required = false) Long hotelId) {
        return analyticsService.getRoomTypeDistribution(hotelId);
    }

    @GetMapping("/service-revenue")
    public List<Map<String, Object>> getServiceRevenue(
            @RequestParam(required = false) Long hotelId) {
        return analyticsService.getServiceRevenue(hotelId);
    }

    @GetMapping("/hotel-revenue")
    public List<Map<String, Object>> getHotelRevenue() {
        return analyticsService.getHotelRevenue();
    }

    @GetMapping("/key-metrics")
    public Map<String, Object> getKeyMetrics(@RequestParam(required = false) Long hotelId) {
        return analyticsService.getKeyMetrics(hotelId);
    }

    @GetMapping("/occupancy-rate")
    public Map<String, Object> getOccupancyRate(@RequestParam(required = false) Long hotelId) {
        return analyticsService.getOccupancyRate(hotelId);
    }

    @GetMapping("/guest-stats")
    public Map<String, Object> getGuestStatistics(@RequestParam(required = false) Long hotelId) {
        return analyticsService.getGuestStatistics(hotelId);
    }

    @GetMapping("/reservation-stats")
    public Map<String, Object> getReservationStatistics(@RequestParam(required = false) Long hotelId) {
        return analyticsService.getReservationStatistics(hotelId);
    }
}