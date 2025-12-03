package com.example.demo.Services;

import com.example.demo.DTOs.RevenueStatsDTO;
import com.example.demo.Entities.Hotel;
import com.example.demo.Entities.HotelRevenueStats;
import com.example.demo.Entities.PeriodType;
import com.example.demo.Repositories.HotelRevenueStatsRepository;
import com.example.demo.Repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RevenueStatsServiceImpl implements RevenueStatsService {

    @Autowired
    private HotelRevenueStatsRepository revenueStatsRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void calculateMonthlyStats(Long hotelId, Integer year, Integer month) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));

        LocalDate statPeriod = LocalDate.of(year, month, 1);

        Optional<HotelRevenueStats> existingStats = revenueStatsRepository
                .findByHotelHotelIdAndStatPeriodAndPeriodType(hotelId, statPeriod, PeriodType.monthly);

        HotelRevenueStats stats = existingStats.orElse(new HotelRevenueStats(hotel, statPeriod, PeriodType.monthly));

        calculateAndSetMonthlyStats(stats, hotelId, year, month);

        revenueStatsRepository.save(stats);
    }

    @Override
    public void calculateYearlyStats(Long hotelId, Integer year) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));

        LocalDate statPeriod = LocalDate.of(year, 1, 1);

        Optional<HotelRevenueStats> existingStats = revenueStatsRepository
                .findByHotelHotelIdAndStatPeriodAndPeriodType(hotelId, statPeriod, PeriodType.yearly);

        HotelRevenueStats stats = existingStats.orElse(new HotelRevenueStats(hotel, statPeriod, PeriodType.yearly));

        calculateYearlyStatsFromMonthly(stats, hotelId, year);

        revenueStatsRepository.save(stats);
    }

    @Override
    public void initializeAllStats() {
        List<Hotel> hotels = hotelRepository.findAll();
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        for (Hotel hotel : hotels) {
            for (int year = 2022; year <= currentYear; year++) {
                int startMonth = (year == 2022) ? 1 : 1;
                int endMonth = (year == currentYear) ? currentMonth : 12;

                for (int month = startMonth; month <= endMonth; month++) {
                    calculateMonthlyStats(hotel.getHotelId(), year, month);
                }

                calculateYearlyStats(hotel.getHotelId(), year);
            }
        }
    }

    private void calculateAndSetMonthlyStats(HotelRevenueStats stats, Long hotelId, Integer year, Integer month) {
        // Try to use the PostgreSQL function first
        try {
            String functionCall = "SELECT calculate_monthly_revenue(?, ?, ?)";
            entityManager.createNativeQuery(functionCall)
                    .setParameter(1, hotelId)
                    .setParameter(2, year)
                    .setParameter(3, month)
                    .executeUpdate();

            entityManager.flush();
            entityManager.refresh(stats);
        } catch (Exception e) {
            // Fallback to manual calculation
            calculateMonthlyStatsManually(stats, hotelId, year, month);
        }
    }

    private void calculateMonthlyStatsManually(HotelRevenueStats stats, Long hotelId, Integer year, Integer month) {
        // Using date_trunc for PostgreSQL compatibility
        String sql = """
            WITH monthly_data AS (
                SELECT 
                    COALESCE(SUM(DISTINCT i.total_amount), 0) as room_revenue,
                    COALESCE(SUM(sc.amount), 0) as service_revenue,
                    COUNT(DISTINCT s.stay_id) as total_stays,
                    COUNT(DISTINCT rr.room_id) as total_rooms,
                    COUNT(sc.charge_id) as total_services
                FROM stay s
                JOIN invoice i ON s.stay_id = i.stay_id
                JOIN reservation r ON s.reservation_id = r.reservation_id
                JOIN reservation_room rr ON r.reservation_id = rr.reservation_id
                JOIN room rm ON rr.room_id = rm.room_id
                LEFT JOIN service_charge sc ON s.stay_id = sc.stay_id
                WHERE rm.hotel_id = ?1
                    AND DATE_TRUNC('month', s.actual_check_in) = DATE_TRUNC('month', DATE(?2 || '-' || ?3 || '-01'))
                    AND r.status IN ('CHECKED_OUT', 'BOOKED', 'CHECKED_IN')
            )
            SELECT 
                room_revenue,
                service_revenue,
                room_revenue + service_revenue as total_revenue,
                total_stays,
                total_rooms,
                total_services
            FROM monthly_data
            """;

        // Create the date string for PostgreSQL
        String dateStr = String.format("%d-%02d-01", year, month);

        try {
            @SuppressWarnings("unchecked")
            List<Object[]> results = entityManager.createNativeQuery(sql)
                    .setParameter(1, hotelId)
                    .setParameter(2, year)
                    .setParameter(3, month)
                    .getResultList();

            if (!results.isEmpty() && results.get(0) != null) {
                Object[] row = results.get(0);

                BigDecimal roomRevenue = getBigDecimalFromObject(row[0]);
                BigDecimal serviceRevenue = getBigDecimalFromObject(row[1]);
                BigDecimal totalRevenue = getBigDecimalFromObject(row[2]);
                Integer totalStays = getIntegerFromObject(row[3]);
                Integer totalRooms = getIntegerFromObject(row[4]);
                Integer totalServices = getIntegerFromObject(row[5]);

                YearMonth yearMonth = YearMonth.of(year, month);
                int daysInMonth = yearMonth.lengthOfMonth();

                BigDecimal avgDailyRevenue = daysInMonth > 0 ?
                        totalRevenue.divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP) :
                        BigDecimal.ZERO;

                BigDecimal avgRoomRate = totalRooms > 0 ?
                        roomRevenue.divide(BigDecimal.valueOf(totalRooms), 2, RoundingMode.HALF_UP) :
                        BigDecimal.ZERO;

                BigDecimal avgServicePerStay = totalStays > 0 ?
                        serviceRevenue.divide(BigDecimal.valueOf(totalStays), 2, RoundingMode.HALF_UP) :
                        BigDecimal.ZERO;

                stats.setRoomRevenue(roomRevenue);
                stats.setServiceRevenue(serviceRevenue);
                stats.setTotalRevenue(totalRevenue);
                stats.setTotalStays(totalStays);
                stats.setTotalRoomsBooked(totalRooms);
                stats.setTotalServices(totalServices);
                stats.setAvgDailyRevenue(avgDailyRevenue);
                stats.setAvgRoomRate(avgRoomRate);
                stats.setAvgServicePerStay(avgServicePerStay);
                stats.setCalculatedAt(LocalDateTime.now());
            } else {
                setZeroStats(stats);
            }
        } catch (Exception e) {
            // If that fails, try an alternative query
            calculateMonthlyStatsAlternative(stats, hotelId, year, month);
        }
    }

    private void calculateMonthlyStatsAlternative(HotelRevenueStats stats, Long hotelId, Integer year, Integer month) {
        // Alternative query using BETWEEN dates
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        String sql = """
            WITH monthly_data AS (
                SELECT 
                    COALESCE(SUM(DISTINCT i.total_amount), 0) as room_revenue,
                    COALESCE(SUM(sc.amount), 0) as service_revenue,
                    COUNT(DISTINCT s.stay_id) as total_stays,
                    COUNT(DISTINCT rr.room_id) as total_rooms,
                    COUNT(sc.charge_id) as total_services
                FROM stay s
                JOIN invoice i ON s.stay_id = i.stay_id
                JOIN reservation r ON s.reservation_id = r.reservation_id
                JOIN reservation_room rr ON r.reservation_id = rr.reservation_id
                JOIN room rm ON rr.room_id = rm.room_id
                LEFT JOIN service_charge sc ON s.stay_id = sc.stay_id
                WHERE rm.hotel_id = ?1
                    AND s.actual_check_in >= ?2
                    AND s.actual_check_in <= ?3
                    AND r.status IN ('CHECKED_OUT', 'BOOKED', 'CHECKED_IN')
            )
            SELECT 
                room_revenue,
                service_revenue,
                room_revenue + service_revenue as total_revenue,
                total_stays,
                total_rooms,
                total_services
            FROM monthly_data
            """;

        try {
            @SuppressWarnings("unchecked")
            List<Object[]> results = entityManager.createNativeQuery(sql)
                    .setParameter(1, hotelId)
                    .setParameter(2, startDate)
                    .setParameter(3, endDate)
                    .getResultList();

            if (!results.isEmpty() && results.get(0) != null) {
                Object[] row = results.get(0);

                BigDecimal roomRevenue = getBigDecimalFromObject(row[0]);
                BigDecimal serviceRevenue = getBigDecimalFromObject(row[1]);
                BigDecimal totalRevenue = getBigDecimalFromObject(row[2]);
                Integer totalStays = getIntegerFromObject(row[3]);
                Integer totalRooms = getIntegerFromObject(row[4]);
                Integer totalServices = getIntegerFromObject(row[5]);

                YearMonth yearMonth = YearMonth.of(year, month);
                int daysInMonth = yearMonth.lengthOfMonth();

                BigDecimal avgDailyRevenue = daysInMonth > 0 ?
                        totalRevenue.divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP) :
                        BigDecimal.ZERO;

                BigDecimal avgRoomRate = totalRooms > 0 ?
                        roomRevenue.divide(BigDecimal.valueOf(totalRooms), 2, RoundingMode.HALF_UP) :
                        BigDecimal.ZERO;

                BigDecimal avgServicePerStay = totalStays > 0 ?
                        serviceRevenue.divide(BigDecimal.valueOf(totalStays), 2, RoundingMode.HALF_UP) :
                        BigDecimal.ZERO;

                stats.setRoomRevenue(roomRevenue);
                stats.setServiceRevenue(serviceRevenue);
                stats.setTotalRevenue(totalRevenue);
                stats.setTotalStays(totalStays);
                stats.setTotalRoomsBooked(totalRooms);
                stats.setTotalServices(totalServices);
                stats.setAvgDailyRevenue(avgDailyRevenue);
                stats.setAvgRoomRate(avgRoomRate);
                stats.setAvgServicePerStay(avgServicePerStay);
                stats.setCalculatedAt(LocalDateTime.now());
            } else {
                setZeroStats(stats);
            }
        } catch (Exception e) {
            // If all else fails, set zero stats
            setZeroStats(stats);
        }
    }

    private void calculateYearlyStatsFromMonthly(HotelRevenueStats yearlyStats, Long hotelId, Integer year) {
        List<HotelRevenueStats> monthlyStats = revenueStatsRepository
                .findMonthlyStatsByHotelAndYear(hotelId, year);

        if (monthlyStats.isEmpty()) {
            setZeroStats(yearlyStats);
            return;
        }

        BigDecimal totalRoomRevenue = BigDecimal.ZERO;
        BigDecimal totalServiceRevenue = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalStays = 0;
        int totalRooms = 0;
        int totalServices = 0;

        for (HotelRevenueStats monthly : monthlyStats) {
            totalRoomRevenue = totalRoomRevenue.add(monthly.getRoomRevenue());
            totalServiceRevenue = totalServiceRevenue.add(monthly.getServiceRevenue());
            totalRevenue = totalRevenue.add(monthly.getTotalRevenue());
            totalStays += monthly.getTotalStays();
            totalRooms += monthly.getTotalRoomsBooked();
            totalServices += monthly.getTotalServices();
        }

        int daysInYear = YearMonth.of(year, 12).atEndOfMonth().getDayOfYear();
        BigDecimal avgDailyRevenue = daysInYear > 0 ?
                totalRevenue.divide(BigDecimal.valueOf(daysInYear), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        BigDecimal avgRoomRate = totalRooms > 0 ?
                totalRoomRevenue.divide(BigDecimal.valueOf(totalRooms), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        BigDecimal avgServicePerStay = totalStays > 0 ?
                totalServiceRevenue.divide(BigDecimal.valueOf(totalStays), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        yearlyStats.setRoomRevenue(totalRoomRevenue);
        yearlyStats.setServiceRevenue(totalServiceRevenue);
        yearlyStats.setTotalRevenue(totalRevenue);
        yearlyStats.setTotalStays(totalStays);
        yearlyStats.setTotalRoomsBooked(totalRooms);
        yearlyStats.setTotalServices(totalServices);
        yearlyStats.setAvgDailyRevenue(avgDailyRevenue);
        yearlyStats.setAvgRoomRate(avgRoomRate);
        yearlyStats.setAvgServicePerStay(avgServicePerStay);
        yearlyStats.setCalculatedAt(LocalDateTime.now());
    }

    private void setZeroStats(HotelRevenueStats stats) {
        stats.setRoomRevenue(BigDecimal.ZERO);
        stats.setServiceRevenue(BigDecimal.ZERO);
        stats.setTotalRevenue(BigDecimal.ZERO);
        stats.setTotalStays(0);
        stats.setTotalRoomsBooked(0);
        stats.setTotalServices(0);
        stats.setAvgDailyRevenue(BigDecimal.ZERO);
        stats.setAvgRoomRate(BigDecimal.ZERO);
        stats.setAvgServicePerStay(BigDecimal.ZERO);
        stats.setCalculatedAt(LocalDateTime.now());
    }

    private RevenueStatsDTO convertToDTO(HotelRevenueStats stats) {
        return RevenueStatsDTO.fromEntity(
                stats.getStatPeriod(),
                stats.getHotel().getName(),
                stats.getRoomRevenue(),
                stats.getServiceRevenue(),
                stats.getTotalRevenue(),
                stats.getAvgDailyRevenue(),
                stats.getAvgRoomRate(),
                stats.getAvgServicePerStay(),
                stats.getTotalStays(),
                stats.getTotalRoomsBooked(),
                stats.getTotalServices()
        );
    }

    private BigDecimal getBigDecimalFromObject(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        if (obj instanceof Number) return new BigDecimal(obj.toString());
        return BigDecimal.ZERO;
    }

    private Integer getIntegerFromObject(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Number) return ((Number) obj).intValue();
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueStatsDTO> getYearlyRevenueStats(Integer hotelId) {
        if (hotelId == -1) {
            // Return aggregated data for ALL hotels
            return getAggregatedYearlyStats();
        } else {
            // Return data for specific hotel
            Long hotelIdLong = hotelId.longValue();
            List<HotelRevenueStats> stats = revenueStatsRepository.findYearlyStats(hotelIdLong);
            return stats.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueStatsDTO> getMonthlyRevenueStats(Integer hotelId, Integer year) {
        if (hotelId == -1) {
            // Return aggregated data for ALL hotels
            return getAggregatedMonthlyStats(year);
        } else {
            // Return data for specific hotel
            Long hotelIdLong = hotelId.longValue();
            List<HotelRevenueStats> stats = revenueStatsRepository.findMonthlyStatsByYear(hotelIdLong, year);
            return stats.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
    }

    // New method: Get aggregated yearly stats for ALL hotels
    private List<RevenueStatsDTO> getAggregatedYearlyStats() {
        // Get stats for all hotels
        List<HotelRevenueStats> allStats = revenueStatsRepository.findYearlyStats(null);

        // Group by year and aggregate
        Map<Integer, AggregatedStats> aggregatedMap = new HashMap<>();

        for (HotelRevenueStats stat : allStats) {
            int year = stat.getStatPeriod().getYear();

            AggregatedStats aggregated = aggregatedMap.getOrDefault(year, new AggregatedStats());
            aggregated.add(stat);
            aggregatedMap.put(year, aggregated);
        }

        // Convert to DTOs and sort by year
        return aggregatedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    int year = entry.getKey();
                    AggregatedStats aggregated = entry.getValue();

                    // Calculate averages
                    BigDecimal avgRoomRate = aggregated.totalRoomsBooked > 0 ?
                            aggregated.totalRoomRevenue.divide(BigDecimal.valueOf(aggregated.totalRoomsBooked), 2, RoundingMode.HALF_UP) :
                            BigDecimal.ZERO;

                    BigDecimal avgServicePerStay = aggregated.totalStays > 0 ?
                            aggregated.totalServiceRevenue.divide(BigDecimal.valueOf(aggregated.totalStays), 2, RoundingMode.HALF_UP) :
                            BigDecimal.ZERO;

                    int daysInYear = YearMonth.of(year, 12).atEndOfMonth().getDayOfYear();
                    BigDecimal avgDailyRevenue = daysInYear > 0 ?
                            aggregated.totalRevenue.divide(BigDecimal.valueOf(daysInYear), 2, RoundingMode.HALF_UP) :
                            BigDecimal.ZERO;

                    return RevenueStatsDTO.fromEntity(
                            LocalDate.of(year, 1, 1),
                            "All Hotels",
                            aggregated.totalRoomRevenue,
                            aggregated.totalServiceRevenue,
                            aggregated.totalRevenue,
                            avgDailyRevenue,
                            avgRoomRate,
                            avgServicePerStay,
                            aggregated.totalStays,
                            aggregated.totalRoomsBooked,
                            aggregated.totalServices
                    );
                })
                .collect(Collectors.toList());
    }

    // New method: Get aggregated monthly stats for ALL hotels
    private List<RevenueStatsDTO> getAggregatedMonthlyStats(Integer year) {
        // Get stats for all hotels for the given year
        List<HotelRevenueStats> allStats = revenueStatsRepository.findMonthlyStatsByYear(null, year);

        // Group by month and aggregate
        Map<Integer, AggregatedStats> aggregatedMap = new HashMap<>();

        for (HotelRevenueStats stat : allStats) {
            int month = stat.getStatPeriod().getMonthValue();

            AggregatedStats aggregated = aggregatedMap.getOrDefault(month, new AggregatedStats());
            aggregated.add(stat);
            aggregatedMap.put(month, aggregated);
        }

        // Convert to DTOs and sort by month
        return aggregatedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    int month = entry.getKey();
                    AggregatedStats aggregated = entry.getValue();

                    // Calculate averages for the month
                    BigDecimal avgRoomRate = aggregated.totalRoomsBooked > 0 ?
                            aggregated.totalRoomRevenue.divide(BigDecimal.valueOf(aggregated.totalRoomsBooked), 2, RoundingMode.HALF_UP) :
                            BigDecimal.ZERO;

                    BigDecimal avgServicePerStay = aggregated.totalStays > 0 ?
                            aggregated.totalServiceRevenue.divide(BigDecimal.valueOf(aggregated.totalStays), 2, RoundingMode.HALF_UP) :
                            BigDecimal.ZERO;

                    YearMonth yearMonth = YearMonth.of(year, month);
                    int daysInMonth = yearMonth.lengthOfMonth();
                    BigDecimal avgDailyRevenue = daysInMonth > 0 ?
                            aggregated.totalRevenue.divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP) :
                            BigDecimal.ZERO;

                    return RevenueStatsDTO.fromEntity(
                            LocalDate.of(year, month, 1),
                            "All Hotels",
                            aggregated.totalRoomRevenue,
                            aggregated.totalServiceRevenue,
                            aggregated.totalRevenue,
                            avgDailyRevenue,
                            avgRoomRate,
                            avgServicePerStay,
                            aggregated.totalStays,
                            aggregated.totalRoomsBooked,
                            aggregated.totalServices
                    );
                })
                .collect(Collectors.toList());
    }

    // Helper class for aggregation
    private static class AggregatedStats {
        BigDecimal totalRoomRevenue = BigDecimal.ZERO;
        BigDecimal totalServiceRevenue = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalStays = 0;
        int totalRoomsBooked = 0;
        int totalServices = 0;

        void add(HotelRevenueStats stat) {
            this.totalRoomRevenue = this.totalRoomRevenue.add(stat.getRoomRevenue());
            this.totalServiceRevenue = this.totalServiceRevenue.add(stat.getServiceRevenue());
            this.totalRevenue = this.totalRevenue.add(stat.getTotalRevenue());
            this.totalStays += stat.getTotalStays();
            this.totalRoomsBooked += stat.getTotalRoomsBooked();
            this.totalServices += stat.getTotalServices();
        }
    }
}