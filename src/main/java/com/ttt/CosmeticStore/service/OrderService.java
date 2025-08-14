//package com.ttt.CosmeticStore.service;
//
//import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
//import com.ttt.CosmeticStore.dto.response.OrderResponse;
//import com.ttt.CosmeticStore.entity.*;
//import com.ttt.CosmeticStore.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//public class OrderService {
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private OrderItemRepository orderItemRepository;
//
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private CartRepository cartRepository;
//
//    @Autowired
//    private CartItemRepository cartItemRepository;
//
//    @Autowired
//    private ShippingAddressRepository shippingAddressRepository;
//
//    @Autowired
//    private ShippingAddressService shippingAddressService;
//
//    @Transactional
//    public OrderResponse createOrder(User user, CheckoutRequest request) {
//        // Xử lý địa chỉ giao hàng
//        ShippingAddress shippingAddress;
//
//        if (request.getShippingAddressId() != null) {
//            // Sử dụng địa chỉ đã có
//            shippingAddress = shippingAddressService.findById(request.getShippingAddressId());
//            // Kiểm tra quyền sở hữu
//            if (!shippingAddress.getUser().getId().equals(user.getId())) {
//                throw new RuntimeException("Không có quyền sử dụng địa chỉ này");
//            }
//        } else if (request.getNewShippingAddress() != null) {
//            // Tạo địa chỉ mới
//            var addressResponse = shippingAddressService.createAddress(user, request.getNewShippingAddress());
//            shippingAddress = shippingAddressService.findById(addressResponse.getId());
//        } else {
//            throw new RuntimeException("Vui lòng chọn địa chỉ giao hàng");
//        }
//
//        // Tạo đơn hàng mới
//        Order order = new Order();
//        order.setUser(user);
//        order.setOrderNumber(generateOrderNumber());
//        order.setShippingAddress(shippingAddress);
//        order.setNote(request.getNote());
//        order.setStatus(Order.OrderStatus.PENDING);
//
//        // Tính tổng tiền và tạo order items
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        List<OrderItem> orderItems = new ArrayList<>();
//
//        for (CheckoutRequest.CheckoutItem item : request.getItems()) {
//            Product product = productRepository.findById(item.getProductId())
//                    .orElseThrow(() -> new RuntimeException("Product not found"));
//
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setProduct(product);
//            orderItem.setQuantity(item.getQuantity());
//            orderItem.setUnitPrice(product.getPrice());
//            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
//
//            orderItems.add(orderItem);
//            totalAmount = totalAmount.add(orderItem.getTotalPrice());
//        }
//
//        order.setTotalAmount(totalAmount);
//        order.setOrderItems(orderItems);
//
//        // Lưu đơn hàng
//        Order savedOrder = orderRepository.save(order);
//
//        // Tạo payment record
//        Payment payment = new Payment();
//        payment.setOrder(savedOrder);
//        payment.setPaymentMethod(request.getPaymentMethod());
//        payment.setAmount(totalAmount);
//        payment.setStatus(Payment.PaymentStatus.PENDING);
//        payment.setTransactionId(generateTransactionId());
//
//        Payment savedPayment = paymentRepository.save(payment);
//        savedOrder.setPayment(savedPayment);
//
//        // Xóa cart items sau khi đặt hàng thành công
//        clearUserCart(user);
//
//        return convertToOrderResponse(savedOrder);
//    }
//
//    @Transactional
//    public OrderResponse processPayment(String orderNumber, String paymentMethod) {
//        Order order = orderRepository.findByOrderNumber(orderNumber)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        Payment payment = order.getPayment();
//
//        // Simulate payment processing
//        boolean paymentSuccess = simulatePaymentProcessing(payment.getAmount(), paymentMethod);
//
//        if (paymentSuccess) {
//            payment.setStatus(Payment.PaymentStatus.COMPLETED);
//            payment.setPaymentDate(LocalDateTime.now());
//            order.setStatus(Order.OrderStatus.CONFIRMED);
//        } else {
//            payment.setStatus(Payment.PaymentStatus.FAILED);
//            order.setStatus(Order.OrderStatus.CANCELLED);
//        }
//
//        paymentRepository.save(payment);
//        Order savedOrder = orderRepository.save(order);
//
//        return convertToOrderResponse(savedOrder);
//    }
//
//    public List<OrderResponse> getUserOrders(User user) {
//        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
//        return orders.stream()
//                .map(this::convertToOrderResponse)
//                .collect(Collectors.toList());
//    }
//
//    public OrderResponse getOrderByNumber(String orderNumber) {
//        Order order = orderRepository.findByOrderNumber(orderNumber)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//        return convertToOrderResponse(order);
//    }
//
//    private String generateOrderNumber() {
//        return "ORD" + System.currentTimeMillis();
//    }
//
//    private String generateTransactionId() {
//        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
//    }
//
//    private boolean simulatePaymentProcessing(BigDecimal amount, String paymentMethod) {
//        // Giả lập xử lý thanh toán - trong thực tế sẽ tích hợp với payment gateway
//        try {
//            Thread.sleep(1000); // Simulate processing time
//            return Math.random() > 0.1; // 90% success rate
//        } catch (InterruptedException e) {
//            return false;
//        }
//    }
//
//    private void clearUserCart(User user) {
//        Cart cart = cartRepository.findByUser(user).orElse(null);
//        if (cart != null) {
//            cartItemRepository.deleteByCart(cart);
//        }
//    }
//
//    private OrderResponse convertToOrderResponse(Order order) {
//        OrderResponse response = new OrderResponse();
//        response.setId(order.getId());
//        response.setOrderNumber(order.getOrderNumber());
//        response.setTotalAmount(order.getTotalAmount());
//        response.setStatus(order.getStatus().name());
//
//        // Chuyển đổi thông tin địa chỉ giao hàng
//        if (order.getShippingAddress() != null) {
//            ShippingAddress addr = order.getShippingAddress();
//            response.setShippingAddress(addr.getFullAddress());
//            response.setPhoneNumber(addr.getPhoneNumber());
//        }
//
//        response.setNote(order.getNote());
//        response.setCreatedAt(order.getCreatedAt());
//
//        // Convert order items
//        List<OrderResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
//                .map(item -> {
//                    OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
//                    itemResponse.setProductId(item.getProduct().getId());
//                    itemResponse.setProductName(item.getProduct().getName());
//                    itemResponse.setQuantity(item.getQuantity());
//                    itemResponse.setUnitPrice(item.getUnitPrice());
//                    itemResponse.setTotalPrice(item.getTotalPrice());
//                    return itemResponse;
//                })
//                .collect(Collectors.toList());
//        response.setItems(itemResponses);
//
//        // Convert payment
//        if (order.getPayment() != null) {
//            OrderResponse.PaymentResponse paymentResponse = new OrderResponse.PaymentResponse();
//            paymentResponse.setPaymentMethod(order.getPayment().getPaymentMethod());
//            paymentResponse.setAmount(order.getPayment().getAmount());
//            paymentResponse.setStatus(order.getPayment().getStatus().name());
//            paymentResponse.setTransactionId(order.getPayment().getTransactionId());
//            paymentResponse.setPaymentDate(order.getPayment().getPaymentDate());
//            response.setPayment(paymentResponse);
//        }
//
//        return response;
//    }
//}
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