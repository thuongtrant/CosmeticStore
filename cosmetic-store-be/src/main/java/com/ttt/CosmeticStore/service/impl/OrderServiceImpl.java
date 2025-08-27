package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.entity.*;
import com.ttt.CosmeticStore.mapper.OrderMapper;
import com.ttt.CosmeticStore.mapper.ShippingAddressMapper;
import com.ttt.CosmeticStore.repository.*;
import com.ttt.CosmeticStore.service.OrderService;
import com.ttt.CosmeticStore.service.ShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private OrderItemRepository orderItemRepository;

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
                    .orElseThrow(() -> new RuntimeException("Product not found"));
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
                throw new RuntimeException("Không có quyền sử dụng địa chỉ này");
            }
        } else if (request.getNewShippingAddress() != null) {
            // Tạo địa chỉ mới
            var addressResponse = shippingAddressService.createAddress(user, request.getNewShippingAddress());
            shippingAddress = shippingAddressService.findById(addressResponse.getId());
        } else {
            throw new RuntimeException("Vui lòng chọn địa chỉ giao hàng");
        }

        // Tạo đơn hàng mới
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(generateOrderNumber());
        order.setShippingAddress(shippingAddress);
        order.setNote(request.getNote());
        order.setStatus(Order.OrderStatus.PENDING);

        // Tính tổng tiền và tạo order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CheckoutRequest.CheckoutItem item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Tính tổng tiền cho item này
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(itemTotal); // Set the total price for this item
            orderItems.add(orderItem);

            // Tính tổng tiền sản phẩm (không có phí ship)
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        // Lưu đơn hàng
        Order savedOrder = orderRepository.save(order);

        // Tạo payment record
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(totalAmount);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setTransactionId(generateTransactionId());

        Payment savedPayment = paymentRepository.save(payment);
        savedOrder.setPayment(savedPayment);

        // Xóa cart items sau khi đặt hàng thành công
        clearUserCart(user);

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
                .orElseThrow(() -> new RuntimeException("Order not found"));
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
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = order.getPayment();

        if ("COMPLETED".equals(status)) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setTransactionId(transactionId);
        } else if ("FAILED".equals(status)) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            order.setStatus(Order.OrderStatus.CANCELLED);
        }

        paymentRepository.save(payment);
        orderRepository.save(order);
    }
}

