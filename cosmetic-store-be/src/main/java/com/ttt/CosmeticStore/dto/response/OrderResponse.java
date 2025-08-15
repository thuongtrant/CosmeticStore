package com.ttt.CosmeticStore.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private String phoneNumber;
    private String note;
    private LocalDateTime createdAt;
    private PaymentResponse payment;
    private List<OrderItemResponse> items;

    @Data
    public static class OrderItemResponse {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private String mainImage;

    }

    @Data
    public static class PaymentResponse {
        private String paymentMethod;
        private BigDecimal amount;
        private String status;
        private String transactionId;
        private LocalDateTime paymentDate;
    }
}
