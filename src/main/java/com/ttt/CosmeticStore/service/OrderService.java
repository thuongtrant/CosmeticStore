package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.entity.User;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(User user, CheckoutRequest request);

    OrderResponse processPayment(String orderNumber, String paymentMethod);

    List<OrderResponse> getUserOrders(User user);

    OrderResponse getOrderByNumber(String orderNumber);
}