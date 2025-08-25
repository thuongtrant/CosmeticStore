package com.ttt.CosmeticStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatisticResponse {
    private Long productId;
    private String productName;
    private String categoryName;
    private Long totalSold;
    private BigDecimal totalRevenue;
    private Integer currentStock;
    private Double avgRating;
    private Long reviewCount;
    private String imageUrl;
}
