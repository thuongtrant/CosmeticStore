package com.ttt.CosmeticStore.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderNumberGenerator {

    public String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
    }

    public String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
