package com.ttt.CosmeticStore.exception;

public class OrderProcessingException extends OrderException {
    public OrderProcessingException(String message) {
        super(message);
    }

    public OrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
