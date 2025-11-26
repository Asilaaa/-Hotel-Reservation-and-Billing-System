package com.example.demo.Services;

import com.example.demo.Repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.math.BigDecimal;

@Service
public class RevenueService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public Map<String, Double> getDailyRevenue(LocalDate startDate, LocalDate endDate, Long hotelId) {
        List<Object[]> results;
        if (hotelId != null) {
            results = invoiceRepository.findDailyRevenueByHotel(startDate, endDate, hotelId);
        } else {
            results = invoiceRepository.findDailyRevenue(startDate, endDate);
        }

        Map<String, Double> revenueMap = new LinkedHashMap<>();
        for (Object[] result : results) {
            LocalDate date = (LocalDate) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            revenueMap.put(date.toString(), amount.doubleValue());
        }
        return revenueMap;
    }

    public Map<String, Double> getMonthlyRevenue(LocalDate startDate, LocalDate endDate, Long hotelId) {
        List<Object[]> results;
        if (hotelId != null) {
            results = invoiceRepository.findMonthlyRevenueByHotel(startDate, endDate, hotelId);
        } else {
            results = invoiceRepository.findMonthlyRevenue(startDate, endDate);
        }

        Map<String, Double> revenueMap = new LinkedHashMap<>();
        for (Object[] result : results) {
            BigDecimal year = (BigDecimal) result[0];
            BigDecimal month = (BigDecimal) result[1];
            BigDecimal amount = (BigDecimal) result[2];

            String key = year.intValue() + "-" + String.format("%02d", month.intValue());
            revenueMap.put(key, amount.doubleValue());
        }
        return revenueMap;
    }

    public Map<String, Double> getYearlyRevenue(LocalDate startDate, LocalDate endDate, Long hotelId) {
        List<Object[]> results;
        if (hotelId != null) {
            results = invoiceRepository.findYearlyRevenueByHotel(startDate, endDate, hotelId);
        } else {
            results = invoiceRepository.findYearlyRevenue(startDate, endDate);
        }

        Map<String, Double> revenueMap = new LinkedHashMap<>();
        for (Object[] result : results) {
            BigDecimal year = (BigDecimal) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            revenueMap.put(String.valueOf(year.intValue()), amount.doubleValue());
        }
        return revenueMap;
    }

    public List<RevenueDataPoint> getRevenueTrend(LocalDate startDate, LocalDate endDate, String period, Long hotelId) {
        List<RevenueDataPoint> trendData = new ArrayList<>();

        // Use wider date range to include all years
        LocalDate actualStartDate = LocalDate.of(2022, 1, 1);
        LocalDate actualEndDate = LocalDate.of(2025, 12, 31);

        switch (period.toLowerCase()) {
            case "daily":
                Map<String, Double> dailyData = getDailyRevenue(actualStartDate, actualEndDate, hotelId);
                dailyData.forEach((date, amount) ->
                        trendData.add(new RevenueDataPoint(date, amount)));
                break;

            case "monthly":
                Map<String, Double> monthlyData = getMonthlyRevenue(actualStartDate, actualEndDate, hotelId);
                monthlyData.forEach((month, amount) ->
                        trendData.add(new RevenueDataPoint(month, amount)));
                break;

            case "yearly":
                Map<String, Double> yearlyData = getYearlyRevenue(actualStartDate, actualEndDate, hotelId);
                yearlyData.forEach((year, amount) ->
                        trendData.add(new RevenueDataPoint(year, amount)));
                break;

            default:
                Map<String, Double> defaultData = getMonthlyRevenue(actualStartDate, actualEndDate, hotelId);
                defaultData.forEach((month, amount) ->
                        trendData.add(new RevenueDataPoint(month, amount)));
                break;
        }

        return trendData;
    }

    public static class RevenueDataPoint {
        private String label;
        private Double amount;

        public RevenueDataPoint() {}

        public RevenueDataPoint(String label, Double amount) {
            this.label = label;
            this.amount = amount;
        }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }
}