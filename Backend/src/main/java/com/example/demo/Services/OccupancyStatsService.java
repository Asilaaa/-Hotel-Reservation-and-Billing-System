package com.example.demo.Services;

import com.example.demo.DTOs.OccupancyStatsDTO;
import com.example.demo.Entities.HotelOccupancyStats;
import com.example.demo.Repositories.HotelOccupancyStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OccupancyStatsService {

    @Autowired
    private HotelOccupancyStatsRepository statsRepository;

    public List<OccupancyStatsDTO> getYearlyOccupancyStats(Long hotelId) {
        List<HotelOccupancyStats> stats;

        if (hotelId == -1) {
            stats = statsRepository.findAllYearlyStats();
        } else {
            stats = statsRepository.findYearlyStatsByHotelId(hotelId);
        }

        return stats.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OccupancyStatsDTO> getMonthlyOccupancyStats(Long hotelId, Integer year) {
        List<HotelOccupancyStats> stats;

        if (hotelId == -1) {
            stats = statsRepository.findMonthlyStatsByYear(year);
        } else {
            stats = statsRepository.findMonthlyStatsByHotelIdAndYear(hotelId, year);
        }

        return stats.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private OccupancyStatsDTO convertToDTO(HotelOccupancyStats stats) {
        return new OccupancyStatsDTO(
                stats.getStatId(),
                stats.getHotel().getHotelId(),
                stats.getHotel().getName(),
                stats.getPeriodType(),
                stats.getPeriodDate(),
                stats.getTotalRooms(),
                stats.getOccupiedRooms(),
                stats.getOccupancyRate(),
                stats.getLastUpdated()
        );
    }
}