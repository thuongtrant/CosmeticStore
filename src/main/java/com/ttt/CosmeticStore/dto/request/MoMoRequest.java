package com.ttt.CosmeticStore.dto.request;

import lombok.Data;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
public class MoMoRequest {

    @JsonProperty("partnerCode")
    private String partnerCode;

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("amount")
    private Long amount;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("orderInfo")
    private String orderInfo;

    @JsonProperty("redirectUrl")
    private String redirectUrl;

    @JsonProperty("ipnUrl")
    private String ipnUrl;

    @JsonProperty("requestType")
    private String requestType;

    @JsonProperty("extraData")
    private String extraData;

    @JsonProperty("lang")
    private String lang;

    @JsonProperty("signature")
    private String signature;
}