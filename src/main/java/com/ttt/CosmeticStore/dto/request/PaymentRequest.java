package com.ttt.CosmeticStore.dto.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderNumber;
    private String paymentMethod;
    private String returnUrl;
    private String cancelUrl;
}
