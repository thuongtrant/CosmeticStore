package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.response.ProductReport;
import com.ttt.CosmeticStore.dto.response.CategoryReport;
import com.ttt.CosmeticStore.dto.response.DailyReport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReportService {

    // Thống kê tổng quan
    BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);
    Long getTotalOrders(LocalDateTime startDate, LocalDateTime endDate);
    Long getTotalUsers();
    Long getTotalProducts();

    // Thống kê đơn hàng
    Map<String, Long> getOrdersByStatus(LocalDateTime startDate, LocalDateTime endDate);
    List<ProductReport> getTopSellingProducts(LocalDateTime startDate, LocalDateTime endDate, int limit);

    // Thống kê danh mục
    List<CategoryReport> getCategoryStats(LocalDateTime startDate, LocalDateTime endDate);

    // Thống kê doanh thu theo thời gian
    List<DailyReport> getDailyRevenue(LocalDate startDate, LocalDate endDate);
    List<DailyReport> getMonthlyRevenue(int months);

    // Dữ liệu cho biểu đồ
    Map<String, Object> getRevenueChartData(LocalDate startDate, LocalDate endDate);
    Map<String, Object> getCategoryChartData(LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Object> getOrderStatusChartData(LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Object> getMonthlyComparison();
    Map<String, Object> getUserGrowthData(int months);

    // Thống kê so sánh
    BigDecimal getRevenueGrowth();
    Long getOrderGrowth();

    // Thống kê khách hàng
    Long getNewCustomers(LocalDateTime startDate, LocalDateTime endDate);
    Long getActiveCustomers(LocalDateTime startDate, LocalDateTime endDate);

    // Thống kê kho hàng
    Long getLowStockProducts(int threshold);
    List<ProductReport> getOutOfStockProducts();
}