package com.ttt.CosmeticStore.dto.request;

import com.ttt.CosmeticStore.validation.ValidCheckoutRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
@ValidCheckoutRequest
public class CheckoutRequest {
    private Long shippingAddressId;

    @Valid
    private ShippingAddressRequest newShippingAddress;

    @Size(max = 500, message = "Ghi chú không quá 500 ký tự")
    private String note;

    @NotBlank(message = "Payment method không được để trống")
    private String paymentMethod;

    @NotBlank(message = "MoMo request type không được để trống")
    private String momoRequestType;

    private List<CheckoutItem> items;
    private boolean preCheckout = false;

    @Data
    public static class CheckoutItem {
        @NotNull(message = "ID sản phẩm không được null")
        @Positive(message = "ID sản phẩm phải là số dương")
        private Long productId;

        @NotNull(message = "Số lượng không được null")
        @Min(value = 1, message = "Số lượng phải ít nhất là 1")
        @Max(value = 999, message = "Số lượng không được vượt quá 999")
        private Integer quantity;
    }
}
