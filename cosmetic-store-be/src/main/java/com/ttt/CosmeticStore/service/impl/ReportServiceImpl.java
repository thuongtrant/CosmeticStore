package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.response.CategoryReport;
import com.ttt.CosmeticStore.dto.response.ProductReport;
import com.ttt.CosmeticStore.entity.Order;
import com.ttt.CosmeticStore.repository.OrderRepository;
import com.ttt.CosmeticStore.repository.ProductRepository;
import com.ttt.CosmeticStore.repository.UserRepository;
import com.ttt.CosmeticStore.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            BigDecimal revenue = orderRepository.getTotalRevenue(startDate, endDate);
            return revenue != null ? revenue : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("Lỗi khi tính tổng doanh thu: ", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Long getTotalOrders(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return orderRepository.countOrdersByDateRange(startDate, endDate);
        } catch (Exception e) {
            log.error("Lỗi khi đếm tổng đơn hàng: ", e);
            return 0L;
        }
    }

    @Override
    public Long getTotalUsers() {
        try {
            return userRepository.count();
        } catch (Exception e) {
            log.error("Lỗi khi đếm tổng người dùng: ", e);
            return 0L;
        }
    }

    @Override
    public Long getTotalProducts() {
        try {
            return productRepository.count();
        } catch (Exception e) {
            log.error("Lỗi khi đếm tổng sản phẩm: ", e);
            return 0L;
        }
    }

    @Override
    public Map<String, Long> getOrdersByStatus(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Object[]> results = orderRepository.countOrdersByStatus(startDate, endDate);
            Map<String, Long> statusMap = new LinkedHashMap<>();

            // Khởi tạo tất cả trạng thái với giá trị 0
            for (Order.OrderStatus status : Order.OrderStatus.values()) {
                statusMap.put(getStatusDisplayName(status), 0L);
            }

            // Cập nhật với dữ liệu thực tế
            for (Object[] result : results) {
                Order.OrderStatus status = (Order.OrderStatus) result[0];
                Long count = ((Number) result[1]).longValue();
                statusMap.put(getStatusDisplayName(status), count);
            }

            return statusMap;
        } catch (Exception e) {
            log.error("Lỗi khi lấy thống kê đơn hàng theo trạng thái: ", e);
            return new HashMap<>();
        }
    }

    @Override
    public List<ProductReport> getTopSellingProducts(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        try {
            List<Object[]> results = orderRepository.getTopSellingProducts(startDate, endDate, limit);
            return results.stream().map(this::mapToProductReport).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm bán chạy: ", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<CategoryReport> getCategoryStats(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Object[]> results = orderRepository.getCategoryStats(startDate, endDate);
            return results.stream().map(this::mapToCategoryReport).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi lấy thống kê danh mục: ", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<com.ttt.CosmeticStore.dto.response.DailyReport> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(23, 59, 59);

            List<Object[]> results = orderRepository.getDailyRevenue(start, end);
            return results.stream().map(this::mapToDailyReport).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi lấy doanh thu theo ngày: ", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<com.ttt.CosmeticStore.dto.response.DailyReport> getMonthlyRevenue(int months) {
        try {
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusMonths(months);

            List<Object[]> results = orderRepository.getMonthlyRevenue(startDate, endDate);
            return results.stream().map(this::mapToMonthlyReport).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi lấy doanh thu theo tháng: ", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getRevenueChartData(LocalDate startDate, LocalDate endDate) {
        try {
            List<com.ttt.CosmeticStore.dto.response.DailyReport> dailyRevenue = getDailyRevenue(startDate, endDate);

            Map<String, Object> chartData = new HashMap<>();
            chartData.put("labels", dailyRevenue.stream()
                .map(com.ttt.CosmeticStore.dto.response.DailyReport::getFormattedDate)
                .collect(Collectors.toList()));
            chartData.put("data", dailyRevenue.stream()
                .map(com.ttt.CosmeticStore.dto.response.DailyReport::getRevenue)
                .collect(Collectors.toList()));

            return chartData;
        } catch (Exception e) {
            log.error("Lỗi khi tạo dữ liệu biểu đồ doanh thu: ", e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getCategoryChartData(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<CategoryReport> categoryStats = getCategoryStats(startDate, endDate);

            Map<String, Object> chartData = new HashMap<>();
            chartData.put("labels", categoryStats.stream()
                .map(CategoryReport::getCategoryName)
                .collect(Collectors.toList()));
            chartData.put("data", categoryStats.stream()
                .map(CategoryReport::getTotalRevenue)
                .collect(Collectors.toList()));

            return chartData;
        } catch (Exception e) {
            log.error("Lỗi khi tạo dữ liệu biểu đồ danh mục: ", e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getOrderStatusChartData(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Map<String, Long> ordersByStatus = getOrdersByStatus(startDate, endDate);

            Map<String, Object> chartData = new HashMap<>();
            chartData.put("labels", new ArrayList<>(ordersByStatus.keySet()));
            chartData.put("data", new ArrayList<>(ordersByStatus.values()));

            return chartData;
        } catch (Exception e) {
            log.error("Lỗi khi tạo dữ liệu biểu đồ trạng thái đơn hàng: ", e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getMonthlyComparison() {
        try {
            LocalDateTime thisMonthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime thisMonthEnd = LocalDateTime.now();
            LocalDateTime lastMonthStart = thisMonthStart.minusMonths(1);
            LocalDateTime lastMonthEnd = thisMonthStart.minusSeconds(1);

            BigDecimal thisMonthRevenue = getTotalRevenue(thisMonthStart, thisMonthEnd);
            BigDecimal lastMonthRevenue = getTotalRevenue(lastMonthStart, lastMonthEnd);

            Map<String, Object> comparison = new HashMap<>();
            comparison.put("thisMonth", thisMonthRevenue);
            comparison.put("lastMonth", lastMonthRevenue);

            if (lastMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal growth = thisMonthRevenue.subtract(lastMonthRevenue)
                    .divide(lastMonthRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
                comparison.put("growthPercent", growth);
            } else {
                comparison.put("growthPercent", BigDecimal.ZERO);
            }

            return comparison;
        } catch (Exception e) {
            log.error("Lỗi khi so sánh doanh thu tháng: ", e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getUserGrowthData(int months) {
        try {
            List<Object[]> monthlyData = new ArrayList<>();
            LocalDateTime endDateTime = LocalDateTime.now();

            for (int i = months - 1; i >= 0; i--) {
                LocalDateTime monthStart = endDateTime.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);

                Long newUsers = getNewCustomers(monthStart, monthEnd);
                String monthLabel = monthStart.format(DateTimeFormatter.ofPattern("MM/yyyy"));

                monthlyData.add(new Object[]{monthLabel, newUsers});
            }

            Map<String, Object> chartData = new HashMap<>();
            chartData.put("labels", monthlyData.stream()
                .map(data -> data[0])
                .collect(Collectors.toList()));
            chartData.put("data", monthlyData.stream()
                .map(data -> data[1])
                .collect(Collectors.toList()));

            return chartData;
        } catch (Exception e) {
            log.error("Lỗi khi lấy dữ liệu tăng trưởng người dùng: ", e);
            return new HashMap<>();
        }
    }

    @Override
    public BigDecimal getRevenueGrowth() {
        try {
            Map<String, Object> comparison = getMonthlyComparison();
            return (BigDecimal) comparison.getOrDefault("growthPercent", BigDecimal.ZERO);
        } catch (Exception e) {
            log.error("Lỗi khi tính tăng trưởng doanh thu: ", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Long getOrderGrowth() {
        try {
            LocalDateTime thisMonthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime thisMonthEnd = LocalDateTime.now();
            LocalDateTime lastMonthStart = thisMonthStart.minusMonths(1);
            LocalDateTime lastMonthEnd = thisMonthStart.minusSeconds(1);

            Long thisMonthOrders = getTotalOrders(thisMonthStart, thisMonthEnd);
            Long lastMonthOrders = getTotalOrders(lastMonthStart, lastMonthEnd);

            return thisMonthOrders - lastMonthOrders;
        } catch (Exception e) {
            log.error("Lỗi khi tính tăng trưởng đơn hàng: ", e);
            return 0L;
        }
    }

    @Override
    public Long getNewCustomers(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return userRepository.countNewUsers(startDate, endDate);
        } catch (Exception e) {
            log.error("Lỗi khi đếm khách hàng mới: ", e);
            return 0L;
        }
    }

    @Override
    public Long getActiveCustomers(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return userRepository.countActiveUsers(startDate, endDate);
        } catch (Exception e) {
            log.error("Lỗi khi đếm khách hàng hoạt động: ", e);
            return 0L;
        }
    }

    @Override
    public Long getLowStockProducts(int threshold) {
        try {
            return productRepository.countLowStockProducts(threshold);
        } catch (Exception e) {
            log.error("Lỗi khi đếm sản phẩm tồn kho thấp: ", e);
            return 0L;
        }
    }

    @Override
    public List<ProductReport> getOutOfStockProducts() {
        try {
            List<Object[]> results = productRepository.getOutOfStockProducts();
            return results.stream().map(this::mapToOutOfStockProductReport).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi lấy sản phẩm hết hàng: ", e);
            return new ArrayList<>();
        }
    }

    // Helper methods
    private String getStatusDisplayName(Order.OrderStatus status) {
        return switch (status) {
            case PENDING -> "Chờ xác nhận";
            case CONFIRMED -> "Đã xác nhận";
            case PROCESSING -> "Đang xử lý";
            case SHIPPED -> "Đang giao";
            case DELIVERED -> "Đã giao";
            case CANCELLED -> "Đã hủy";
        };
    }

    private ProductReport mapToProductReport(Object[] result) {
        return new ProductReport(
            ((Number) result[0]).longValue(),  // productId
            (String) result[1],                // productName
            (String) result[4],                // categoryName
            ((Number) result[2]).longValue(),  // totalQuantity
            (BigDecimal) result[3],            // totalRevenue
            null,                              // currentStock
            null                               // imageUrl
        );
    }

    private CategoryReport mapToCategoryReport(Object[] result) {
        return new CategoryReport(
            ((Number) result[0]).longValue(),  // categoryId
            (String) result[1],                // categoryName
            ((Number) result[2]).longValue(),  // totalQuantity
            (BigDecimal) result[3],            // totalRevenue
            ((Number) result[4]).longValue()   // orderCount
        );
    }

    private com.ttt.CosmeticStore.dto.response.DailyReport mapToDailyReport(Object[] result) {
        LocalDate date = ((java.sql.Date) result[0]).toLocalDate();
        return new com.ttt.CosmeticStore.dto.response.DailyReport(
            date,
            date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            (BigDecimal) result[1],            // revenue
            ((Number) result[2]).longValue(),  // orderCount
            null                               // customerCount
        );
    }

    private com.ttt.CosmeticStore.dto.response.DailyReport mapToMonthlyReport(Object[] result) {
        int year = ((Number) result[0]).intValue();
        int month = ((Number) result[1]).intValue();
        LocalDate date = LocalDate.of(year, month, 1);

        return new com.ttt.CosmeticStore.dto.response.DailyReport(
            date,
            date.format(DateTimeFormatter.ofPattern("MM/yyyy")),
            (BigDecimal) result[2],            // revenue
            ((Number) result[3]).longValue(),  // orderCount
            null                               // customerCount
        );
    }

    private ProductReport mapToOutOfStockProductReport(Object[] result) {
        return new ProductReport(
            ((Number) result[0]).longValue(),  // productId
            (String) result[1],                // productName
            (String) result[3],                // categoryName
            0L,                                // totalQuantity
            BigDecimal.ZERO,                   // totalRevenue
            ((Number) result[2]).intValue(),   // currentStock
            null                               // imageUrl
        );
    }
}