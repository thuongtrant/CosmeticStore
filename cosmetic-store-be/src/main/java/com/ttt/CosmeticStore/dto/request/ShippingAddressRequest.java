package com.ttt.CosmeticStore.dto.request;

import lombok.Data;

@Data
public class ShippingAddressRequest {
    private String recipientName;
    private String phoneNumber;
    private String addressLine;
    private String ward;
    private String district;
    private String province;
    private String postalCode;
    private Boolean isDefault = false;
    private String label;
}
