package com.ttt.CosmeticStore.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public interface ChartDataService {
     Map<String, Object> getRevenueChartData(LocalDate startDate, LocalDate endDate);
    
     Map<String, Object> getCategoryChartData(LocalDateTime startDate, LocalDateTime endDate);
    
     Map<String, Object> getOrderStatusChartData(LocalDateTime startDate, LocalDateTime endDate);

//    private Map<String, Object> createEmptyChartData();
}
