package com.ttt.CosmeticStore.dto.response;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class MoMoResponse {

    @JsonProperty("partnerCode")
    private String partnerCode;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("amount")
    private Long amount;

    @JsonProperty("responseTime")
    private Long responseTime;

    @JsonProperty("message")
    private String message;

    @JsonProperty("resultCode")
    private Integer resultCode;

    @JsonProperty("payUrl")
    private String payUrl;

    @JsonProperty("deeplink")
    private String deeplink;

    @JsonProperty("qrCodeUrl")
    private String qrCodeUrl;
}