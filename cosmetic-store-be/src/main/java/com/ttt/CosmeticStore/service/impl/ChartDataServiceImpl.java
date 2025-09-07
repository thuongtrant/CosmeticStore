package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.response.CategoryReport;
import com.ttt.CosmeticStore.dto.response.DailyReport;
import com.ttt.CosmeticStore.mapper.ReportMapper;
import com.ttt.CosmeticStore.repository.OrderRepository;
import com.ttt.CosmeticStore.service.ChartDataService;
import com.ttt.CosmeticStore.service.ReportDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChartDataServiceImpl implements ChartDataService {

    private final ReportDataService dataService;
    private final OrderRepository orderRepository;
    private final ReportMapper reportMapper;

    public Map<String, Object> getRevenueChartData(LocalDate startDate, LocalDate endDate) {
        try {

            // Get raw data from repository
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(23, 59, 59);
            List<Object[]> rawData = orderRepository.getDailyRevenue(start, end);

            List<DailyReport> dailyReports = rawData.stream()
                    .map(reportMapper::mapToDailyReport)
                    .collect(Collectors.toList());

            // Prepare chart data
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("labels", dailyReports.stream()
                    .map(DailyReport::getFormattedDate)
                    .collect(Collectors.toList()));
            chartData.put("data", dailyReports.stream()
                    .map(DailyReport::getRevenue)
                    .collect(Collectors.toList()));

            return chartData;
        } catch (Exception e) {
            return createEmptyChartData();
        }
    }

    public Map<String, Object> getCategoryChartData(LocalDateTime startDate, LocalDateTime endDate) {
        try {

            // Get category stats
            List<CategoryReport> categoryStats = dataService.getCategoryStats(startDate, endDate);

            Map<String, Object> chartData = new HashMap<>();
            chartData.put("labels", categoryStats.stream()
                    .map(CategoryReport::getCategoryName)
                    .collect(Collectors.toList()));
            chartData.put("data", categoryStats.stream()
                    .map(CategoryReport::getTotalRevenue)
                    .collect(Collectors.toList()));

            return chartData;
        } catch (Exception e) {
            return createEmptyChartData();
        }
    }

    public Map<String, Object> getOrderStatusChartData(LocalDateTime startDate, LocalDateTime endDate) {
        try {

            Map<String, Long> ordersByStatus = dataService.getOrdersByStatus(startDate, endDate);

            Map<String, Object> chartData = new HashMap<>();
            chartData.put("labels", new ArrayList<>(ordersByStatus.keySet()));
            chartData.put("data", new ArrayList<>(ordersByStatus.values()));

            return chartData;
        } catch (Exception e) {
            return createEmptyChartData();
        }
    }

    private Map<String, Object> createEmptyChartData() {
        Map<String, Object> emptyData = new HashMap<>();
        emptyData.put("labels", List.of());
        emptyData.put("data", List.of());
        return emptyData;
    }
}
