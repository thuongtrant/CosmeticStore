package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.response.CategoryReport;
import com.ttt.CosmeticStore.dto.response.DailyReport;
import com.ttt.CosmeticStore.dto.response.ProductReport;
import com.ttt.CosmeticStore.service.ChartDataService;
import com.ttt.CosmeticStore.service.ReportDataService;
import com.ttt.CosmeticStore.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportDataService dataService;
    private final ChartDataService chartService;

    @Override
    public BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return dataService.getTotalRevenue(startDate, endDate);
    }

    @Override
    public Long getTotalOrders(LocalDateTime startDate, LocalDateTime endDate) {
        return dataService.getTotalOrders(startDate, endDate);
    }

    @Override
    public Long getTotalUsers() {
        return dataService.getTotalUsers();
    }

    @Override
    public Long getTotalProducts() {
        return dataService.getTotalProducts();
    }

    // Order statistics
    @Override
    public Map<String, Long> getOrdersByStatus(LocalDateTime startDate, LocalDateTime endDate) {
        return dataService.getOrdersByStatus(startDate, endDate);
    }

    // Product statistics
    @Override
    public List<ProductReport> getTopSellingProducts(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        return dataService.getTopSellingProducts(startDate, endDate, limit);
    }

    @Override
    public List<ProductReport> getOutOfStockProducts() {
        return dataService.getOutOfStockProducts();
    }

    @Override
    public Long getLowStockProducts(int threshold) {
        return dataService.getLowStockProducts(threshold);
    }

    // Category statistics
    @Override
    public List<CategoryReport> getCategoryStats(LocalDateTime startDate, LocalDateTime endDate) {
        return dataService.getCategoryStats(startDate, endDate);
    }

    // Revenue reports
    @Override
    public List<DailyReport> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        return dataService.getDailyRevenue(startDate, endDate);
    }

    @Override
    public List<DailyReport> getMonthlyRevenue(int months) {
        // This method needs implementation in ReportDataService
        log.warn("getMonthlyRevenue not yet implemented in ReportDataService");
        return List.of();
    }

    // Chart data - delegate to ChartDataService
    @Override
    public Map<String, Object> getRevenueChartData(LocalDate startDate, LocalDate endDate) {
        return chartService.getRevenueChartData(startDate, endDate);
    }

    @Override
    public Map<String, Object> getCategoryChartData(LocalDateTime startDate, LocalDateTime endDate) {
        return chartService.getCategoryChartData(startDate, endDate);
    }

    @Override
    public Map<String, Object> getOrderStatusChartData(LocalDateTime startDate, LocalDateTime endDate) {
        return chartService.getOrderStatusChartData(startDate, endDate);
    }

    // Customer statistics
    @Override
    public Long getNewCustomers(LocalDateTime startDate, LocalDateTime endDate) {
        return dataService.getNewCustomers(startDate, endDate);
    }

    @Override
    public Long getActiveCustomers(LocalDateTime startDate, LocalDateTime endDate) {
        return dataService.getActiveCustomers(startDate, endDate);
    }

    // Growth analysis - simplified implementations
    @Override
    public Map<String, Object> getMonthlyComparison() {
        try {
            LocalDateTime thisMonthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime thisMonthEnd = LocalDateTime.now();
            LocalDateTime lastMonthStart = thisMonthStart.minusMonths(1);
            LocalDateTime lastMonthEnd = thisMonthStart.minusSeconds(1);

            BigDecimal thisMonthRevenue = dataService.getTotalRevenue(thisMonthStart, thisMonthEnd);
            BigDecimal lastMonthRevenue = dataService.getTotalRevenue(lastMonthStart, lastMonthEnd);

            return Map.of(
                "thisMonth", thisMonthRevenue,
                "lastMonth", lastMonthRevenue,
                "growthPercent", calculateGrowthPercent(thisMonthRevenue, lastMonthRevenue)
            );
        } catch (Exception e) {
            log.error("Error in monthly comparison: ", e);
            return Map.of();
        }
    }

    @Override
    public Map<String, Object> getUserGrowthData(int months) {
        log.warn("getUserGrowthData simplified implementation");
        return Map.of("labels", List.of(), "data", List.of());
    }

    @Override
    public BigDecimal getRevenueGrowth() {
        Map<String, Object> comparison = getMonthlyComparison();
        return (BigDecimal) comparison.getOrDefault("growthPercent", BigDecimal.ZERO);
    }

    @Override
    public Long getOrderGrowth() {
        try {
            LocalDateTime thisMonthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime thisMonthEnd = LocalDateTime.now();
            LocalDateTime lastMonthStart = thisMonthStart.minusMonths(1);
            LocalDateTime lastMonthEnd = thisMonthStart.minusSeconds(1);

            Long thisMonthOrders = dataService.getTotalOrders(thisMonthStart, thisMonthEnd);
            Long lastMonthOrders = dataService.getTotalOrders(lastMonthStart, lastMonthEnd);

            return thisMonthOrders - lastMonthOrders;
        } catch (Exception e) {
            log.error("Error calculating order growth: ", e);
            return 0L;
        }
    }

    private BigDecimal calculateGrowthPercent(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) > 0) {
            return current.subtract(previous)
                .divide(previous, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
}