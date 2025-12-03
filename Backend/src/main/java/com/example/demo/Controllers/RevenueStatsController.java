package com.example.demo.Controllers;

import com.example.demo.DTOs.RevenueStatsDTO;
import com.example.demo.Entities.Hotel;
import com.example.demo.Repositories.HotelRevenueStatsRepository;
import com.example.demo.Repositories.HotelRepository;
import com.example.demo.Services.RevenueStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics/revenue")
@CrossOrigin(origins = "http://localhost:3000")
public class RevenueStatsController {

    @Autowired
    private RevenueStatsService revenueStatsService;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelRevenueStatsRepository revenueStatsRepository;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Revenue Stats Service is running at " + LocalDateTime.now());
    }

    @GetMapping("/yearly/{hotelId}")
    public ResponseEntity<List<RevenueStatsDTO>> getYearlyRevenueStats(
            @PathVariable Integer hotelId) {
        try {
            List<RevenueStatsDTO> stats = revenueStatsService.getYearlyRevenueStats(hotelId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/monthly/{hotelId}")
    public ResponseEntity<List<RevenueStatsDTO>> getMonthlyRevenueStats(
            @PathVariable Integer hotelId,
            @RequestParam Integer year) {
        try {
            List<RevenueStatsDTO> stats = revenueStatsService.getMonthlyRevenueStats(hotelId, year);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/initialize")
    public ResponseEntity<String> initializeStats() {
        try {
            revenueStatsService.initializeAllStats();
            return ResponseEntity.ok("Revenue statistics initialized successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to initialize statistics: " + e.getMessage());
        }
    }

    @PostMapping("/calculate/monthly/{hotelId}/{year}/{month}")
    public ResponseEntity<String> calculateMonthlyStats(
            @PathVariable Long hotelId,
            @PathVariable Integer year,
            @PathVariable Integer month) {
        try {
            revenueStatsService.calculateMonthlyStats(hotelId, year, month);
            return ResponseEntity.ok("Monthly stats calculated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to calculate monthly stats: " + e.getMessage());
        }
    }

    @PostMapping("/calculate/yearly/{hotelId}/{year}")
    public ResponseEntity<String> calculateYearlyStats(
            @PathVariable Long hotelId,
            @PathVariable Integer year) {
        try {
            revenueStatsService.calculateYearlyStats(hotelId, year);
            return ResponseEntity.ok("Yearly stats calculated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to calculate yearly stats: " + e.getMessage());
        }
    }

    @GetMapping("/debug/hotels")
    public ResponseEntity<List<Map<String, Object>>> debugHotels() {
        try {
            List<Hotel> hotels = hotelRepository.findAll();
            List<Map<String, Object>> result = hotels.stream()
                    .map(hotel -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", hotel.getHotelId());
                        map.put("name", hotel.getName());
                        map.put("address", hotel.getAddress());
                        return map;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of(error));
        }
    }

    @GetMapping("/debug/stats-count")
    public ResponseEntity<Map<String, Object>> debugStatsCount() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("totalStats", revenueStatsRepository.count());
            response.put("yearlyStats", revenueStatsRepository.findYearlyStats(null).size());
            response.put("monthlyStats2024", revenueStatsRepository.findMonthlyStatsByYear(null, 2024).size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/debug/test-data")
    public ResponseEntity<Map<String, Object>> testData() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Test database connection
            long hotelCount = hotelRepository.count();
            long statsCount = revenueStatsRepository.count();

            response.put("hotelCount", hotelCount);
            response.put("revenueStatsCount", statsCount);
            response.put("status", "SUCCESS");
            response.put("timestamp", LocalDateTime.now());

            // Test if hotels exist
            if (hotelCount > 0) {
                Hotel firstHotel = hotelRepository.findAll().get(0);
                response.put("sampleHotel", firstHotel.getName());
                response.put("sampleHotelId", firstHotel.getHotelId());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}