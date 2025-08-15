package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.entity.Order;
import com.ttt.CosmeticStore.entity.OrderItem;
import com.ttt.CosmeticStore.entity.ShippingAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toOrderResponse(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus().name());
        response.setNote(order.getNote());
        response.setCreatedAt(order.getCreatedAt());

        // Map shipping address
        mapShippingAddress(order, response);

        // Map order items
        response.setItems(mapOrderItems(order.getOrderItems()));

        // Map payment
        mapPayment(order, response);

        return response;
    }


    public List<OrderResponse> toOrderResponseList(List<Order> orders) {
        if (orders == null) {
            return null;
        }

        return orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }


    private void mapShippingAddress(Order order, OrderResponse response) {
        if (order.getShippingAddress() != null) {
            ShippingAddress addr = order.getShippingAddress();
            response.setShippingAddress(addr.getFullAddress());
            response.setPhoneNumber(addr.getPhoneNumber());
        }
    }


    private List<OrderResponse.OrderItemResponse> mapOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return null;
        }

        return orderItems.stream()
                .map(this::mapOrderItem)
                .collect(Collectors.toList());
    }


    private OrderResponse.OrderItemResponse mapOrderItem(OrderItem item) {
        if (item == null) {
            return null;
        }

        OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
        itemResponse.setProductId(item.getProduct().getId());
        itemResponse.setProductName(item.getProduct().getName());
        itemResponse.setQuantity(item.getQuantity());
        itemResponse.setUnitPrice(item.getUnitPrice());
        itemResponse.setTotalPrice(item.getTotalPrice());
        itemResponse.setMainImage(item.getProduct().getMainImageUrl());
        return itemResponse;
    }


    private void mapPayment(Order order, OrderResponse response) {
        if (order.getPayment() != null) {
            OrderResponse.PaymentResponse paymentResponse = new OrderResponse.PaymentResponse();
            paymentResponse.setPaymentMethod(order.getPayment().getPaymentMethod());
            paymentResponse.setAmount(order.getPayment().getAmount());
            paymentResponse.setStatus(order.getPayment().getStatus().name());
            paymentResponse.setTransactionId(order.getPayment().getTransactionId());
            paymentResponse.setPaymentDate(order.getPayment().getPaymentDate());
            response.setPayment(paymentResponse);
        }
    }
}
