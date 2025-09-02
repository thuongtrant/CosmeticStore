package com.ttt.CosmeticStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummary {
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long totalUsers;
    private Long totalProducts;
    private Long newCustomers;
    private Long activeCustomers;
    private Long lowStockCount;

    private Map<String, Long> ordersByStatus;
    private List<ProductReport> topProducts;
    private List<CategoryReport> categoryStats;
    private List<DailyReport> dailyRevenue;
    private List<ProductReport> outOfStockProducts;

    // Growth metrics
    private BigDecimal revenueGrowth;
    private Long orderGrowth;

    // Helper methods for view
    public String getFormattedRevenue() {
        if (totalRevenue == null) return "0 VNĐ";
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(totalRevenue);
    }

    public String getFormattedRevenueGrowth() {
        if (revenueGrowth == null) return "0%";
        return String.format("%.1f%%", revenueGrowth);
    }

    public String getOrderGrowthDisplay() {
        if (orderGrowth == null) return "0";
        return orderGrowth > 0 ? "+" + orderGrowth : orderGrowth.toString();
    }

    public boolean hasData() {
        return totalRevenue != null && totalRevenue.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasOrders() {
        return totalOrders != null && totalOrders > 0;
    }

    public boolean hasTopProducts() {
        return topProducts != null && !topProducts.isEmpty();
    }

    public boolean hasDailyRevenue() {
        return dailyRevenue != null && !dailyRevenue.isEmpty();
    }
}
