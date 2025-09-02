package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.dto.response.OrdersResponseA;
import com.ttt.CosmeticStore.entity.Order;
import com.ttt.CosmeticStore.mapper.OrderMapper;
import com.ttt.CosmeticStore.repository.OrderRepository;
import com.ttt.CosmeticStore.service.AdminOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Page<OrdersResponseA> getAllOrders(Pageable pageable) {
        return orderRepository.getOrdersForAd(pageable);

    }

    @Override
    public Page<OrdersResponseA> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);

    }

    @Override
    public Page<OrdersResponseA> searchOrdersByOrderNumber(String orderNumber, Pageable pageable) {
        return orderRepository.findByOrderNumber(orderNumber, pageable);

    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra logic chuyển trạng thái hợp lệ
        validateStatusTransition(order.getStatus(), newStatus);

        orderRepository.updateStatus(orderId, newStatus, LocalDateTime.now());

        Order updated = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        return orderMapper.toOrderResponse(updated);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Chỉ có thể hủy đơn hàng ở trạng thái PENDING hoặc CONFIRMED
        if (order.getStatus() != Order.OrderStatus.PENDING &&
                order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new RuntimeException("Không thể hủy đơn hàng ở trạng thái: " + order.getStatus());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        String note = order.getNote();
        if (note == null) note = "";
        order.setNote(note + "\nLý do hủy: " + reason.trim());

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(savedOrder);
    }



    /**
     * Kiểm tra tính hợp lệ của việc chuyển trạng thái
     */
    private void validateStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                if (newStatus != Order.OrderStatus.CONFIRMED && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new RuntimeException("Từ trạng thái PENDING chỉ có thể chuyển sang CONFIRMED hoặc CANCELLED");
                }
                break;
            case CONFIRMED:
                if (newStatus != Order.OrderStatus.PROCESSING && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new RuntimeException("Từ trạng thái CONFIRMED chỉ có thể chuyển sang PROCESSING hoặc CANCELLED");
                }
                break;
            case PROCESSING:
                if (newStatus != Order.OrderStatus.SHIPPED) {
                    throw new RuntimeException("Từ trạng thái PROCESSING chỉ có thể chuyển sang SHIPPED");
                }
                break;
            case SHIPPED:
                if (newStatus != Order.OrderStatus.DELIVERED) {
                    throw new RuntimeException("Từ trạng thái SHIPPED chỉ có thể chuyển sang DELIVERED");
                }
                break;
            case DELIVERED:
            case CANCELLED:
                throw new RuntimeException("Không thể thay đổi trạng thái từ " + currentStatus);
        }
    }
}