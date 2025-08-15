package com.ttt.CosmeticStore.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShippingAddressResponse {
    private Long id;
    private String recipientName;
    private String phoneNumber;
    private String addressLine;
    private String ward;
    private String district;
    private String province;
    private String postalCode;
    private Boolean isDefault;
    private String label;
    private String fullAddress;
    private LocalDateTime createdAt;
}
