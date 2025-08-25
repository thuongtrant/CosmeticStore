package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.response.ProductStatisticResponse;
import com.ttt.CosmeticStore.dto.response.RevenueStatisticResponse;
import com.ttt.CosmeticStore.entity.Order;
import com.ttt.CosmeticStore.repository.OrderRepository;
import com.ttt.CosmeticStore.repository.OrderItemRepository;
import com.ttt.CosmeticStore.repository.ProductRepository;
import com.ttt.CosmeticStore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<RevenueStatisticResponse> getRevenueStatistics(LocalDate startDate, LocalDate endDate, String period) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        // Lấy dữ liệu đơn hàng đã hoàn thành trong khoảng thời gian
        List<Object[]> results = orderRepository.findRevenueStatistics(startDateTime, endDateTime, Order.OrderStatus.DELIVERED);

        return results.stream().map(result -> {
            LocalDate date = ((java.sql.Date) result[0]).toLocalDate();
            BigDecimal revenue = (BigDecimal) result[1];
            Long orderCount = (Long) result[2];
            Long customerCount = (Long) result[3];

            String formattedDate = formatDateByPeriod(date, period);

            return new RevenueStatisticResponse(date, period, revenue, orderCount, customerCount, formattedDate);
        }).collect(Collectors.toList());
    }

    public List<ProductStatisticResponse> getTopSellingProducts(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = orderItemRepository.findTopSellingProducts(startDateTime, endDateTime, "DELIVERED", pageable);

        return results.stream().map(result -> {
            Long productId = (Long) result[0];
            String productName = (String) result[1];
            String categoryName = (String) result[2];
            Long totalSold = (Long) result[3];
            BigDecimal totalRevenue = (BigDecimal) result[4];
            Integer currentStock = (Integer) result[5];
            String imageUrl = (String) result[6];

            return new ProductStatisticResponse(productId, productName, categoryName,
                    totalSold, totalRevenue, currentStock, 0.0, 0L, imageUrl);
        }).collect(Collectors.toList());
    }

    public List<ProductStatisticResponse> getLowStockProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = productRepository.findLowStockProducts(pageable);

        return results.stream().map(result -> {
            Long productId = (Long) result[0];
            String productName = (String) result[1];
            String categoryName = (String) result[2];
            Integer currentStock = (Integer) result[3];
            String imageUrl = (String) result[4];

            return new ProductStatisticResponse(productId, productName, categoryName,
                    0L, BigDecimal.ZERO, currentStock, 0.0, 0L, imageUrl);
        }).collect(Collectors.toList());
    }

    public BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        BigDecimal result = orderRepository.getTotalRevenue(startDateTime, endDateTime, Order.OrderStatus.DELIVERED);
        return result != null ? result : BigDecimal.ZERO;
    }

    public Long getTotalOrders(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        return orderRepository.countByCreatedAtBetweenAndStatus(startDateTime, endDateTime, Order.OrderStatus.DELIVERED);
    }

    public Long getTotalCustomers(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        return orderRepository.countDistinctCustomers(startDateTime, endDateTime, Order.OrderStatus.DELIVERED);
    }

    private String formatDateByPeriod(LocalDate date, String period) {
        switch (period.toLowerCase()) {
            case "day":
                return date.format(DateTimeFormatter.ofPattern("dd/MM"));
            case "week":
                return "Tuần " + date.format(DateTimeFormatter.ofPattern("w/yyyy"));
            case "month":
                return date.format(DateTimeFormatter.ofPattern("MM/yyyy"));
            case "year":
                return date.format(DateTimeFormatter.ofPattern("yyyy"));
            default:
                return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
    }
}
