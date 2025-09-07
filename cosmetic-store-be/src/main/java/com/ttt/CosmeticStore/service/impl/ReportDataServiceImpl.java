package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.response.*;
import com.ttt.CosmeticStore.mapper.ReportMapper;
import com.ttt.CosmeticStore.repository.OrderRepository;
import com.ttt.CosmeticStore.repository.ProductRepository;
import com.ttt.CosmeticStore.repository.UserRepository;
import com.ttt.CosmeticStore.service.ReportDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportDataServiceImpl implements ReportDataService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;
    // Basic data retrieval - delegate to repositories
    public BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            BigDecimal revenue = orderRepository.getTotalRevenue(startDate, endDate);
            return revenue != null ? revenue : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("Error calculating total revenue: ", e);
            return BigDecimal.ZERO;
        }
    }

    public Long getTotalOrders(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return orderRepository.countOrdersByDateRange(startDate, endDate);
        } catch (Exception e) {
            log.error("Error counting total orders: ", e);
            return 0L;
        }
    }

    public Long getTotalUsers() {
        try {
            return userRepository.count();
        } catch (Exception e) {
            log.error("Error counting total users: ", e);
            return 0L;
        }
    }

    public Long getTotalProducts() {
        try {
            return productRepository.count();
        } catch (Exception e) {
            log.error("Error counting total products: ", e);
            return 0L;
        }
    }

    public Long getNewCustomers(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return userRepository.countNewUsers(startDate, endDate);
        } catch (Exception e) {
            log.error("Error counting new customers: ", e);
            return 0L;
        }
    }

    public Long getActiveCustomers(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return userRepository.countActiveUsers(startDate, endDate);
        } catch (Exception e) {
            log.error("Error counting active customers: ", e);
            return 0L;
        }
    }

    public Long getLowStockProducts(int threshold) {
        try {
            return productRepository.countLowStockProducts(threshold);
        } catch (Exception e) {
            log.error("Error counting low stock products: ", e);
            return 0L;
        }
    }

    // Complex data operations - use mapping service
    public Map<String, Long> getOrdersByStatus(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Object[]> results = orderRepository.countOrdersByStatus(startDate, endDate);
            Map<String, Long> statusMap = new LinkedHashMap<>();

            // Initialize all statuses with 0
            for (com.ttt.CosmeticStore.entity.Order.OrderStatus status : com.ttt.CosmeticStore.entity.Order.OrderStatus.values()) {
                statusMap.put(reportMapper.getStatusDisplayName(status), 0L);
            }

            // Update with actual data
            for (Object[] result : results) {
                com.ttt.CosmeticStore.entity.Order.OrderStatus status = (com.ttt.CosmeticStore.entity.Order.OrderStatus) result[0];
                Long count = ((Number) result[1]).longValue();
                statusMap.put(reportMapper.getStatusDisplayName(status), count);
            }

            return statusMap;
        } catch (Exception e) {
            // Return default data instead of empty map
            Map<String, Long> defaultMap = new LinkedHashMap<>();
            defaultMap.put("Chờ xử lý", 0L);
            defaultMap.put("Đã xác nhận", 0L);
            defaultMap.put("Đang xử lý", 0L);
            defaultMap.put("Đã gửi", 0L);
            defaultMap.put("Đã giao", 0L);
            defaultMap.put("Đã hủy", 0L);
            return defaultMap;
        }
    }

    public List<ProductReport> getTopSellingProducts(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        List<Object[]> results = orderRepository.getTopSellingProducts(startDate, endDate, limit);
        return results.stream()
                .map(reportMapper::mapToProductReport)
                .collect(Collectors.toList());
    }

    public List<CategoryReport> getCategoryStats(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Object[]> results = orderRepository.getCategoryStats(startDate, endDate);
            return results.stream()
                    .map(reportMapper::mapToCategoryReport)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting category stats: ", e);
            return List.of();
        }
    }

    public List<DailyReport> getDailyRevenue(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        try {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(23, 59, 59);

            List<Object[]> results = orderRepository.getDailyRevenue(start, end);
            return results.stream()
                    .map(reportMapper::mapToDailyReport)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting daily revenue: ", e);
            return List.of();
        }
    }

    public List<ProductReport> getOutOfStockProducts() {
        try {
            List<Object[]> results = productRepository.getOutOfStockProducts();
            return results.stream()
                    .map(reportMapper::mapToProductReport)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting out of stock products: ", e);
            return List.of();
        }
    }

    // Main summary method
    public ReportSummary getReportSummary(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating report summary for period: {} to {}", startDate, endDate);

        try {
            return ReportSummary.builder()
                    .totalRevenue(getTotalRevenue(startDate, endDate))
                    .totalOrders(getTotalOrders(startDate, endDate))
                    .totalUsers(getTotalUsers())
                    .totalProducts(getTotalProducts())
                    .newCustomers(getNewCustomers(startDate, endDate))
                    .activeCustomers(getActiveCustomers(startDate, endDate))
                    .lowStockCount(getLowStockProducts(10))
                    .ordersByStatus(getOrdersByStatus(startDate, endDate))
                    .topProducts(getTopSellingProducts(startDate, endDate, 10))
                    .categoryStats(getCategoryStats(startDate, endDate))
                    .dailyRevenue(getDailyRevenue(startDate.toLocalDate(), endDate.toLocalDate()))
                    .outOfStockProducts(getOutOfStockProducts())
                    .build();
        } catch (Exception e) {
            log.error("Error generating report summary: ", e);
            return createDefaultSummary();
        }
    }

    private ReportSummary createDefaultSummary() {
        return ReportSummary.builder()
                .totalRevenue(BigDecimal.ZERO)
                .totalOrders(0L)
                .totalUsers(0L)
                .totalProducts(0L)
                .newCustomers(0L)
                .activeCustomers(0L)
                .lowStockCount(0L)
                .ordersByStatus(Map.of())
                .topProducts(List.of())
                .categoryStats(List.of())
                .dailyRevenue(List.of())
                .outOfStockProducts(List.of())
                .build();
    }
}


