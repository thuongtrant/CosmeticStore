package com.ttt.CosmeticStore.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private String paymentMethod;
    private BigDecimal amount;
    private PaymentStatus  status;
    private String transactionId;
    private LocalDateTime paymentDate;
    public enum PaymentStatus {
        PENDING("Chờ thanh toán"),
        COMPLETED("Đã thanh toán"),
        FAILED("Thanh toán thất bại"),
        REFUNDED("Đã hoàn tiền");

        private final String displayName;

        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }

        @JsonValue
        public String getDisplayName() {
            return displayName;
        }
    }

}