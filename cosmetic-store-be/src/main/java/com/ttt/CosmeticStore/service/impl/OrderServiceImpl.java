package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.entity.*;
import com.ttt.CosmeticStore.exception.AddressException;
import com.ttt.CosmeticStore.exception.OrderException;
import com.ttt.CosmeticStore.exception.OrderProcessingException;
import com.ttt.CosmeticStore.exception.ProductNotFoundException;
import com.ttt.CosmeticStore.mapper.OrderMapper;
import com.ttt.CosmeticStore.repository.*;
import com.ttt.CosmeticStore.service.OrderService;
import com.ttt.CosmeticStore.service.ShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ShippingAddressService shippingAddressService;

    @Autowired
    private OrderMapper orderMapper;
    @Override
    public BigDecimal calculateTotalAmount(CheckoutRequest request) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CheckoutRequest.CheckoutItem item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        return totalAmount;
    }
    @Override
    @Transactional
    public OrderResponse createOrder(User user, CheckoutRequest request) {
        // Xử lý địa chỉ giao hàng
        ShippingAddress shippingAddress;
        if (request.getShippingAddressId() != null) {
            // Sử dụng địa chỉ đã có
            shippingAddress = shippingAddressService.findById(request.getShippingAddressId());
            // Kiểm tra quyền sở hữu
            if (!shippingAddress.getUser().getId().equals(user.getId())) {
                throw new AddressException("Không có quyền sử dụng địa chỉ này");
            }
        } else if (request.getNewShippingAddress() != null) {
            // Tạo địa chỉ mới
            var addressResponse = shippingAddressService.createAddress(user, request.getNewShippingAddress());
            shippingAddress = shippingAddressService.findById(addressResponse.getId());
        } else {
            throw new AddressException("Vui lòng chọn địa chỉ giao hàng");
        }

        // Tính tổng tiền
        BigDecimal totalAmount = calculateTotalAmount(request);

        // Tạo đơn hàng mới bằng mapper
        String orderNumber = generateOrderNumber();
        Order order = orderMapper.toOrder(request, user, shippingAddress, orderNumber, totalAmount);

        // Tạo order items bằng mapper
        List<OrderItem> orderItems = orderMapper.toOrderItems(request.getItems(), order, productRepository);
        order.setOrderItems(orderItems);

        // Lưu đơn hàng
        Order savedOrder = orderRepository.save(order);

        // Tạo payment record bằng mapper
        Payment payment = orderMapper.toPayment(savedOrder, request, totalAmount, generateTransactionId());
        Payment savedPayment = paymentRepository.save(payment);
        savedOrder.setPayment(savedPayment);

        // Xóa cart items sau khi đặt hàng thành công
        clearUserCart(user);

        // Ánh xạ sang OrderResponse bằng mapper
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getMyOrders(User user) {
        List<Order> orders = orderRepository.myOrders(user);
        return orderMapper.toOrderResponseList(orders);
    }

    @Override
    public OrderResponse orderDetail(String orderNumber) {
        Order order = orderRepository.getOrderDetail(orderNumber)
                .orElseThrow(() -> new OrderException("Khong tim thay don hang: " + orderNumber));
        return orderMapper.toOrderResponse(order);
    }

    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
    }

    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }


    private void clearUserCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
        if (cart != null) {
            cartItemRepository.deleteByCart(cart);
        }
    }

    @Override
    @Transactional
    public void updatePaymentStatus(String orderNumber, String status, String transactionId) {
        Order order = orderRepository.getOrderDetail(orderNumber)
                .orElseThrow(() -> new OrderException("Khong thay don hang: " + orderNumber));

        Payment payment = order.getPayment();
        try {
            Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status);
            payment.setStatus(paymentStatus);
            if (paymentStatus == Payment.PaymentStatus.COMPLETED) {
                payment.setPaymentDate(LocalDateTime.now());
                payment.setTransactionId(transactionId);
            } else if (paymentStatus == Payment.PaymentStatus.FAILED) {
                order.setStatus(Order.OrderStatus.CANCELLED);
            }
        } catch (IllegalArgumentException e) {
            throw new OrderException("Invalid payment status: " + status, e);
        } catch (Exception e) {
            throw new OrderProcessingException("Failed to update payment status for order: " + orderNumber, e);
        }

        paymentRepository.save(payment);
        orderRepository.save(order);
    }
}

