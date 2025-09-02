package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.response.CategoryReport;
import com.ttt.CosmeticStore.dto.response.DailyReport;
import com.ttt.CosmeticStore.dto.response.ProductReport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReportService {

    // Basic statistics
    BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);
    Long getTotalOrders(LocalDateTime startDate, LocalDateTime endDate);
    Long getTotalUsers();
    Long getTotalProducts();

    // Order statistics
    Map<String, Long> getOrdersByStatus(LocalDateTime startDate, LocalDateTime endDate);

    // Product statistics
    List<ProductReport> getTopSellingProducts(LocalDateTime startDate, LocalDateTime endDate, int limit);
    List<ProductReport> getOutOfStockProducts();
    Long getLowStockProducts(int threshold);

    // Category statistics
    List<CategoryReport> getCategoryStats(LocalDateTime startDate, LocalDateTime endDate);

    // Revenue reports
    List<DailyReport> getDailyRevenue(LocalDate startDate, LocalDate endDate);
    List<DailyReport> getMonthlyRevenue(int months);

    // Chart data
    Map<String, Object> getRevenueChartData(LocalDate startDate, LocalDate endDate);
    Map<String, Object> getCategoryChartData(LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Object> getOrderStatusChartData(LocalDateTime startDate, LocalDateTime endDate);

    // Customer statistics
    Long getNewCustomers(LocalDateTime startDate, LocalDateTime endDate);
    Long getActiveCustomers(LocalDateTime startDate, LocalDateTime endDate);

    // Growth analysis
    Map<String, Object> getMonthlyComparison();
    Map<String, Object> getUserGrowthData(int months);
    BigDecimal getRevenueGrowth();
    Long getOrderGrowth();
}