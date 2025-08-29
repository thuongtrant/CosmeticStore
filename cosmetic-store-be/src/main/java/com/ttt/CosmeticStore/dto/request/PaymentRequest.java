package com.ttt.CosmeticStore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class PaymentRequest {
    @NotBlank(message = "Order number không được để trống")
    private String orderNumber;

    @NotBlank(message = "Payment method không được để trống")
    private String paymentMethod;
    private String returnUrl;
    private String cancelUrl;
}
