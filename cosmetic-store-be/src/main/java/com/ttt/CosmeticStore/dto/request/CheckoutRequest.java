package com.ttt.CosmeticStore.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequest {
    private Long shippingAddressId;
    private ShippingAddressRequest newShippingAddress;
    private String note;
    private String paymentMethod;
    private String momoRequestType;
    private List<CheckoutItem> items;
    private boolean preCheckout = false;

    @Data
    public static class CheckoutItem {
        private Long productId;
        private Integer quantity;
    }
}
