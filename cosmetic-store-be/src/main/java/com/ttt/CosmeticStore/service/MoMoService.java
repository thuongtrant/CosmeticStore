package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.dto.request.MoMoRequest;
import com.ttt.CosmeticStore.dto.response.MoMoResponse;

import java.util.Map;

public interface MoMoService {
    MoMoResponse createPayment(String orderNumber, Long amount, String orderInfo);
    MoMoResponse createPayment(String orderNumber, Long amount, String orderInfo, String requestType);
    boolean verifySignature(String signature, String rawData);
    String generateSignature(String rawData);
}