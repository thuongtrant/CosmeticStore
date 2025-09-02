package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.response.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface ReportDataService {
     BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);

     Long getTotalOrders(LocalDateTime startDate, LocalDateTime endDate);

     Long getTotalUsers();

     Long getTotalProducts();

     Long getNewCustomers(LocalDateTime startDate, LocalDateTime endDate);

     Long getActiveCustomers(LocalDateTime startDate, LocalDateTime endDate);

     Long getLowStockProducts(int threshold);

     Map<String, Long> getOrdersByStatus(LocalDateTime startDate, LocalDateTime endDate);

     List<ProductReport> getTopSellingProducts(LocalDateTime startDate, LocalDateTime endDate, int limit);

     List<CategoryReport> getCategoryStats(LocalDateTime startDate, LocalDateTime endDate);

     List<DailyReport> getDailyRevenue(java.time.LocalDate startDate, java.time.LocalDate endDate);

     List<ProductReport> getOutOfStockProducts();

     ReportSummary getReportSummary(LocalDateTime startDate, LocalDateTime endDate);

//     ReportSummary createDefaultSummary();
}