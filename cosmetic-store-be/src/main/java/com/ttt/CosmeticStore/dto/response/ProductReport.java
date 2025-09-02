package com.ttt.CosmeticStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReport {
    private Long productId;
    private String productName;
    private String categoryName;
    private Long totalQuantity;
    private BigDecimal totalRevenue;
    private Integer currentStock;
    private String imageUrl;
}
