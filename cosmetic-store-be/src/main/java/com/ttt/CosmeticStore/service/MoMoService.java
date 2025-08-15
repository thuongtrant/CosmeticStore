package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.MoMoRequest;
import com.ttt.CosmeticStore.dto.response.MoMoResponse;

public interface MoMoService {
    MoMoResponse createPayment(String orderNumber, Long amount, String orderInfo);
    boolean verifySignature(String signature, String rawData);
    String generateSignature(String rawData);
}