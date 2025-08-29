package com.ttt.CosmeticStore.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String phoneNumber;
    private String note;
    private LocalDateTime createdAt;
    private PaymentResponse payment;
    private List<OrderItemResponse> items;

    public enum OrderStatus {
        PENDING("Chờ xử lý"),
        CONFIRMED("Đã xác nhận"),
        PROCESSING("Đang xử lý"),
        SHIPPED("Đã giao hàng"),
        DELIVERED("Đã nhận hàng"),
        CANCELLED("Đã hủy");

        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        @JsonValue
        public String getDisplayName() {
            return displayName;
        }
    }
}
