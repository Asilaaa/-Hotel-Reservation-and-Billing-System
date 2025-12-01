package com.example.demo.Controllers;

import com.example.demo.DTOs.OccupancyStatsDTO;
import com.example.demo.Services.OccupancyStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics/occupancy")
@CrossOrigin(origins = "http://localhost:3000")
public class OccupancyStatsController {

    @Autowired
    private OccupancyStatsService occupancyStatsService;

    @GetMapping("/yearly/{hotelId}")
    public ResponseEntity<List<OccupancyStatsDTO>> getYearlyOccupancyStats(
            @PathVariable Long hotelId) {
        try {
            List<OccupancyStatsDTO> stats = occupancyStatsService.getYearlyOccupancyStats(hotelId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/monthly/{hotelId}")
    public ResponseEntity<List<OccupancyStatsDTO>> getMonthlyOccupancyStats(
            @PathVariable Long hotelId,
            @RequestParam Integer year) {
        try {
            List<OccupancyStatsDTO> stats = occupancyStatsService.getMonthlyOccupancyStats(hotelId, year);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}