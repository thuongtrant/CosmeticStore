package com.ttt.CosmeticStore.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long id;
    private Long userId;
    private String username;
    private List<CartItemResponse> cartItems = new ArrayList<>();
    private BigDecimal totalAmount;
    private Integer totalQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private BigDecimal productPrice;
        private String productImageUrl;
        private String categoryName;
        private Integer quantity;
        private BigDecimal subtotal;
        private Boolean inStock;
        private Integer availableQuantity;
        private LocalDateTime createdAt;
    }
}