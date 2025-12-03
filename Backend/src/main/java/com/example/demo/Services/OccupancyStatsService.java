package com.example.demo.Services;

import com.example.demo.DTOs.OccupancyStatsDTO;
import com.example.demo.Entities.HotelOccupancyStats;
import com.example.demo.Repositories.HotelOccupancyStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OccupancyStatsService {

    @Autowired
    private HotelOccupancyStatsRepository statsRepository;

    public List<OccupancyStatsDTO> getYearlyOccupancyStats(Long hotelId) {
        if (hotelId == -1) {
            // Return aggregated data for ALL hotels
            return getAggregatedYearlyStats();
        } else {
            // Return data for specific hotel
            return statsRepository.findYearlyStatsByHotelId(hotelId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    }

    public List<OccupancyStatsDTO> getMonthlyOccupancyStats(Long hotelId, Integer year) {
        if (hotelId == -1) {
            // Return aggregated data for ALL hotels
            return getAggregatedMonthlyStats(year);
        } else {
            // Return data for specific hotel
            return statsRepository.findMonthlyStatsByHotelIdAndYear(hotelId, year).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    }

    // New method: Get aggregated yearly stats for ALL hotels
    private List<OccupancyStatsDTO> getAggregatedYearlyStats() {
        List<HotelOccupancyStats> allStats = statsRepository.findAllYearlyStats();

        // Group by year and aggregate
        Map<Integer, AggregatedOccupancyStats> aggregatedMap = new HashMap<>();

        for (HotelOccupancyStats stat : allStats) {
            int year = stat.getPeriodDate().getYear();

            AggregatedOccupancyStats aggregated = aggregatedMap.getOrDefault(year, new AggregatedOccupancyStats());
            aggregated.add(stat);
            aggregatedMap.put(year, aggregated);
        }

        // Convert to DTOs and sort by year
        return aggregatedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    int year = entry.getKey();
                    AggregatedOccupancyStats aggregated = entry.getValue();

                    // Calculate overall occupancy rate
                    double occupancyRate = aggregated.totalRooms > 0 ?
                            (aggregated.occupiedRooms * 100.0) / aggregated.totalRooms : 0;

                    return new OccupancyStatsDTO(
                            null, // statId is null for aggregated data
                            -1L,  // hotelId -1 for "All Hotels"
                            "All Hotels",
                            "yearly",
                            LocalDate.of(year, 1, 1),
                            aggregated.totalRooms,
                            aggregated.occupiedRooms,
                            occupancyRate,
                            LocalDate.now()  // Using LocalDate.now() for aggregated data
                    );
                })
                .collect(Collectors.toList());
    }

    // New method: Get aggregated monthly stats for ALL hotels
    private List<OccupancyStatsDTO> getAggregatedMonthlyStats(Integer year) {
        List<HotelOccupancyStats> allStats = statsRepository.findMonthlyStatsByYear(year);

        // Group by month and aggregate
        Map<Integer, AggregatedOccupancyStats> aggregatedMap = new HashMap<>();

        for (HotelOccupancyStats stat : allStats) {
            int month = stat.getPeriodDate().getMonthValue();

            AggregatedOccupancyStats aggregated = aggregatedMap.getOrDefault(month, new AggregatedOccupancyStats());
            aggregated.add(stat);
            aggregatedMap.put(month, aggregated);
        }

        // Convert to DTOs and sort by month
        return aggregatedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    int month = entry.getKey();
                    AggregatedOccupancyStats aggregated = entry.getValue();

                    // Calculate overall occupancy rate
                    double occupancyRate = aggregated.totalRooms > 0 ?
                            (aggregated.occupiedRooms * 100.0) / aggregated.totalRooms : 0;

                    return new OccupancyStatsDTO(
                            null, // statId is null for aggregated data
                            -1L,  // hotelId -1 for "All Hotels"
                            "All Hotels",
                            "monthly",
                            LocalDate.of(year, month, 1),
                            aggregated.totalRooms,
                            aggregated.occupiedRooms,
                            occupancyRate,
                            LocalDate.now()  // Using LocalDate.now() for aggregated data
                    );
                })
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
                stats.getLastUpdated().toLocalDate()  // Convert LocalDateTime to LocalDate
        );
    }

    // Helper class for aggregation
    private static class AggregatedOccupancyStats {
        int totalRooms = 0;
        int occupiedRooms = 0;

        void add(HotelOccupancyStats stat) {
            this.totalRooms += stat.getTotalRooms();
            this.occupiedRooms += stat.getOccupiedRooms();
        }
    }
}