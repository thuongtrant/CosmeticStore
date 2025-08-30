package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;

public interface PaymentSessionService {
     String createSession(CheckoutRequest request, String username);
     CheckoutRequest getCheckoutRequest(String sessionId);

     String getSessionUser(String sessionId);

     void markOrderCompleted(String sessionId, String orderNumber);

     String getCompletedOrder(String sessionId);

     boolean hasActiveSession(String sessionId);
     void cleanupSession(String sessionId);
}
