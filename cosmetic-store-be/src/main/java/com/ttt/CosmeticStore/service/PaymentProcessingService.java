package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.dto.response.MoMoResponse;
import com.ttt.CosmeticStore.entity.User;
import org.springframework.http.ResponseEntity;


public interface PaymentProcessingService {

     ResponseEntity<?> processMoMoPayment(CheckoutRequest request, User user);

     ResponseEntity<?> processCODPayment(CheckoutRequest request, User user);

//     ResponseEntity<?> buildMoMoResponse(String sessionId, MoMoResponse momoResponse);
}
