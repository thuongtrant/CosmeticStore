//package com.ttt.CosmeticStore.controller.admin;
//
//import com.ttt.CosmeticStore.dto.response.OrderResponse;
//import com.ttt.CosmeticStore.entity.Order;
//import com.ttt.CosmeticStore.entity.ShippingAddress;
//import com.ttt.CosmeticStore.repository.OrderRepository;
//import com.ttt.CosmeticStore.service.OrderService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/admin/orders")
//@CrossOrigin(origins = "*")
//public class AdminOrderController {
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private OrderService orderService;
//
//    @GetMapping
//    public ResponseEntity<?> getAllOrders(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) String status) {
//        try {
//            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
//            Page<Order> orders;
//
//            if (status != null && !status.isEmpty()) {
//                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
//                orders = orderRepository.findAll(pageRequest);
//                // Filter by status (simplified - in real app would use JPA specification)
//            } else {
//                orders = orderRepository.findAll(pageRequest);
//            }
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("orders", orders.getContent().stream()
//                    .map(this::convertToOrderResponse)
//                    .collect(Collectors.toList()));
//            response.put("totalElements", orders.getTotalElements());
//            response.put("totalPages", orders.getTotalPages());
//            response.put("currentPage", page);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", "Lấy danh sách đơn hàng thất bại: " + e.getMessage());
//
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @PutMapping("/{orderId}/status")
//    public ResponseEntity<?> updateOrderStatus(
//            @PathVariable Long orderId,
//            @RequestParam String status) {
//        try {
//            Order order = orderRepository.findById(orderId)
//                    .orElseThrow(() -> new RuntimeException("Order not found"));
//
//            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
//            order.setStatus(newStatus);
//
//            Order savedOrder = orderRepository.save(order);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "Cập nhật trạng thái đơn hàng thành công");
//            response.put("order", convertToOrderResponse(savedOrder));
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", "Cập nhật trạng thái thất bại: " + e.getMessage());
//
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @GetMapping("/{orderId}")
//    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) {
//        try {
//            Order order = orderRepository.findById(orderId)
//                    .orElseThrow(() -> new RuntimeException("Order not found"));
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("order", convertToOrderResponse(order));
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", "Không tìm thấy đơn hàng: " + e.getMessage());
//
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @GetMapping("/statistics")
//    public ResponseEntity<?> getOrderStatistics() {
//        try {
//            long totalOrders = orderRepository.count();
//            long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
//            long confirmedOrders = orderRepository.countByStatus(Order.OrderStatus.CONFIRMED);
//            long shippedOrders = orderRepository.countByStatus(Order.OrderStatus.SHIPPED);
//            long deliveredOrders = orderRepository.countByStatus(Order.OrderStatus.DELIVERED);
//            long cancelledOrders = orderRepository.countByStatus(Order.OrderStatus.CANCELLED);
//
//            Map<String, Object> statistics = new HashMap<>();
//            statistics.put("total", totalOrders);
//            statistics.put("pending", pendingOrders);
//            statistics.put("confirmed", confirmedOrders);
//            statistics.put("shipped", shippedOrders);
//            statistics.put("delivered", deliveredOrders);
//            statistics.put("cancelled", cancelledOrders);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("statistics", statistics);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", "Lấy thống kê thất bại: " + e.getMessage());
//
//            return ResponseEntity.badRequest().body(response);
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
