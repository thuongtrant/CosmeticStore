package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.dto.response.OrdersResponseA;
import com.ttt.CosmeticStore.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminOrderService {

    Page<OrdersResponseA> getAllOrders(Pageable pageable);

    Page<OrdersResponseA> getOrdersByStatus(Order.OrderStatus status, Pageable pageable);

    Page<OrdersResponseA> searchOrdersByOrderNumber(String orderNumber, Pageable pageable);

    OrderResponse getOrderById(Long id);

    OrderResponse updateOrderStatus(Long orderId, Order.OrderStatus newStatus);

    OrderResponse cancelOrder(Long orderId, String reason);


}