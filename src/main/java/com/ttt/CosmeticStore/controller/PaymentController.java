package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.service.OrderService;
import com.ttt.CosmeticStore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @RequestBody CheckoutRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            OrderResponse order = orderService.createOrder(user, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đặt hàng thành công");
            response.put("order", order);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Đặt hàng thất bại: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/process/{orderNumber}")
    public ResponseEntity<?> processPayment(
            @PathVariable String orderNumber,
            @RequestParam String paymentMethod) {
        try {
            OrderResponse order = orderService.processPayment(orderNumber, paymentMethod);

            Map<String, Object> response = new HashMap<>();
            if ("COMPLETED".equals(order.getPayment().getStatus())) {
                response.put("success", true);
                response.put("message", "Thanh toán thành công");
            } else {
                response.put("success", false);
                response.put("message", "Thanh toán thất bại");
            }
            response.put("order", order);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Xử lý thanh toán thất bại: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            List<OrderResponse> orders = orderService.getUserOrders(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lấy danh sách đơn hàng thất bại: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/order/{orderNumber}")
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderNumber) {
        try {
            OrderResponse order = orderService.getOrderByNumber(orderNumber);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("order", order);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không tìm thấy đơn hàng: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/methods")
    public ResponseEntity<?> getPaymentMethods() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("methods", List.of(
            Map.of("id", "COD", "name", "Thanh toán khi nhận hàng", "description", "Thanh toán bằng tiền mặt khi nhận hàng"),
            Map.of("id", "BANK_TRANSFER", "name", "Chuyển khoản ngân hàng", "description", "Chuyển khoản qua tài khoản ngân hàng"),
            Map.of("id", "MOMO", "name", "Ví MoMo", "description", "Thanh toán qua ví điện tử MoMo"),
            Map.of("id", "VNPAY", "name", "VNPay", "description", "Thanh toán qua cổng VNPay")
        ));

        return ResponseEntity.ok(response);
    }
}
