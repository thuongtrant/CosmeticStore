package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.entity.Order;
import com.ttt.CosmeticStore.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    BigDecimal calculateTotalAmount(CheckoutRequest request);

    OrderResponse createOrder(User user, CheckoutRequest request);

    List<OrderResponse> getMyOrders(User user);

    OrderResponse orderDetail(String orderNumber);
    void updatePaymentStatus(String orderNumber, String status, String transactionId);

}

