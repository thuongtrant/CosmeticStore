package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.dto.response.OrderItemResponse;
import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.dto.response.PaymentResponse;
import com.ttt.CosmeticStore.entity.*;
import com.ttt.CosmeticStore.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        response.setStatus(mapOrderStatus(order.getStatus()));
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

    private List<OrderItemResponse> mapOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return null;
        }

        return orderItems.stream()
                .map(this::mapOrderItem)
                .collect(Collectors.toList());
    }

    private OrderItemResponse mapOrderItem(OrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemResponse itemResponse = new OrderItemResponse();
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
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setPaymentMethod(order.getPayment().getPaymentMethod());
            paymentResponse.setAmount(order.getPayment().getAmount());
            paymentResponse.setStatus(PaymentResponse.PaymentStatus.valueOf(order.getPayment().getStatus().name()));
            paymentResponse.setTransactionId(order.getPayment().getTransactionId());
            paymentResponse.setPaymentDate(order.getPayment().getPaymentDate());
            response.setPayment(paymentResponse);
        }
    }

    private OrderResponse.OrderStatus mapOrderStatus(Order.OrderStatus status) {
        if (status == null) {
            return null;
        }
        return OrderResponse.OrderStatus.valueOf(status.name());
    }

    public Order toOrder(CheckoutRequest request, User user, ShippingAddress shippingAddress, String orderNumber, BigDecimal totalAmount) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(orderNumber);
        order.setShippingAddress(shippingAddress);
        order.setNote(request.getNote());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }

    public List<OrderItem> toOrderItems(List<CheckoutRequest.CheckoutItem> checkoutItems, Order order, ProductRepository productRepository) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CheckoutRequest.CheckoutItem item : checkoutItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    public Payment toPayment(Order order, CheckoutRequest request, BigDecimal totalAmount, String transactionId) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(totalAmount);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setTransactionId(transactionId);
        return payment;
    }
}