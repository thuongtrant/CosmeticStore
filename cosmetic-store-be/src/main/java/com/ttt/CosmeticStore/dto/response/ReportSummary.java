package com.ttt.CosmeticStore.dto.response;

import java.math.BigDecimal;

public class ReportSummary {
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long totalUsers;
    private Long totalProducts;
    private BigDecimal revenueGrowth;
    private Long orderGrowth;
    private Long newCustomers;
    private Long activeCustomers;
    private Long lowStockProducts;
}
