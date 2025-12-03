package com.example.demo.Services;


import com.example.demo.DTOs.RevenueStatsDTO;

import java.util.List;

public interface RevenueStatsService {

    List<RevenueStatsDTO> getYearlyRevenueStats(Integer hotelId);

    List<RevenueStatsDTO> getMonthlyRevenueStats(Integer hotelId, Integer year);

    void calculateMonthlyStats(Long hotelId, Integer year, Integer month);

    void calculateYearlyStats(Long hotelId, Integer year);

    void initializeAllStats();
}