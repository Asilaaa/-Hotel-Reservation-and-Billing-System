package com.example.demo.Controllers;

import com.example.demo.Services.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/revenue")
@CrossOrigin(origins = "http://localhost:3000")
public class RevenueController {

    @Autowired
    private RevenueService revenueService;

    @GetMapping("/daily")
    public Map<String, Double> getDailyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long hotelId) {
        return revenueService.getDailyRevenue(startDate, endDate, hotelId);
    }

    @GetMapping("/monthly")
    public Map<String, Double> getMonthlyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long hotelId) {
        return revenueService.getMonthlyRevenue(startDate, endDate, hotelId);
    }

    @GetMapping("/yearly")
    public Map<String, Double> getYearlyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long hotelId) {
        return revenueService.getYearlyRevenue(startDate, endDate, hotelId);
    }

    @GetMapping("/trend")
    public List<RevenueService.RevenueDataPoint> getRevenueTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String period,
            @RequestParam(required = false) Long hotelId) {
        return revenueService.getRevenueTrend(startDate, endDate, period, hotelId);
    }
}