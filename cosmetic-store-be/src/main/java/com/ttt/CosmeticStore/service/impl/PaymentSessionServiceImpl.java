package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.service.PaymentSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PaymentSessionServiceImpl implements PaymentSessionService {
    private final Map<String, CheckoutRequest> checkoutSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionUsers = new ConcurrentHashMap<>();
    private final Map<String, String> completedOrders = new ConcurrentHashMap<>();

    public String createSession(CheckoutRequest request, String username) {
        String sessionId = "CHECKOUT_" + System.currentTimeMillis();
        checkoutSessions.put(sessionId, request);
        sessionUsers.put(sessionId, username);
        return sessionId;
    }

    public CheckoutRequest getCheckoutRequest(String sessionId) {
        return checkoutSessions.get(sessionId);
    }

    public String getSessionUser(String sessionId) {
        return sessionUsers.get(sessionId);
    }

    public void markOrderCompleted(String sessionId, String orderNumber) {
        completedOrders.put(sessionId, orderNumber);
    }

    public String getCompletedOrder(String sessionId) {
        return completedOrders.get(sessionId);
    }

    public boolean hasActiveSession(String sessionId) {
        return checkoutSessions.containsKey(sessionId);
    }

    public void cleanupSession(String sessionId) {
        checkoutSessions.remove(sessionId);
        sessionUsers.remove(sessionId);
        log.info("Cleaned up session: {}", sessionId);
    }
}
