package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.dto.response.OrdersResponseC;
import com.ttt.CosmeticStore.dto.response.PagedResponse;
import com.ttt.CosmeticStore.entity.Order;
import com.ttt.CosmeticStore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    BigDecimal calculateTotalAmount(CheckoutRequest request);

    OrderResponse createOrder(User user, CheckoutRequest request);

    PagedResponse<OrdersResponseC> getMyOrders(User user, int pageable, int size);

    OrderResponse orderDetail(String orderNumber);
    void updatePaymentStatus(String orderNumber, String status, String transactionId);

}

