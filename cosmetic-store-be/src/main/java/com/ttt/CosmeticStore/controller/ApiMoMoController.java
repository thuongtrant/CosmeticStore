package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.service.OrderService;
import com.ttt.CosmeticStore.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/payment/momo")
public class ApiMoMoController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApiCheckoutController checkoutController;

    private final Map<String, String> completedOrders = new ConcurrentHashMap<>();

    @PostMapping("/callback")
    public ResponseEntity<?> handleMoMoCallback(@RequestBody Map<String, Object> callbackData) {
        try {
            String sessionId = String.valueOf(callbackData.get("orderId"));
            Integer resultCode = parseResultCode(callbackData.get("resultCode"));

            // Process successful payment
            if (resultCode == 0 && sessionId != null && sessionId.startsWith("CHECKOUT_")) {
                processSuccessfulPayment(sessionId, callbackData);
            } else if (resultCode != 0) {
                // Clean up failed session
                cleanupSession(sessionId);
            }

            return ResponseEntity.ok(Map.of("resultCode", 0, "message", "IPN received"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("resultCode", 0, "message", "IPN received with error"));
        }
    }

    @GetMapping("/return")
    public ResponseEntity<?> handleMoMoReturn(@RequestParam String orderId,
                                              @RequestParam Integer resultCode,
                                              @RequestParam(required = false) String message) {
        try {
            if (resultCode != 0) {
                cleanupSession(orderId);
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "status", "FAILED",
                        "message", "Thanh toán thất bại: " + (message != null ? message : "Unknown error"),
                        "redirectToCheckout", true
                ));
            }

            if (orderId.startsWith("CHECKOUT_")) {
                return handleCheckoutReturn(orderId);
            }

            // Direct order lookup
            OrderResponse order = orderService.orderDetail(orderId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "status", "COMPLETED",
                    "message", "Thanh toán thành công",
                    "order", order
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "status", "ERROR",
                    "message", "Lỗi xử lý kết quả thanh toán"
            ));
        }
    }

    @GetMapping("/check-order/{sessionId}")
    public ResponseEntity<?> checkOrder(@PathVariable String sessionId) {
        if (!sessionId.startsWith("CHECKOUT_")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "status", "INVALID_SESSION",
                    "message", "Session không hợp lệ"
            ));
        }

        String orderNumber = completedOrders.get(sessionId);
        if (orderNumber != null) {
            try {
                OrderResponse order = orderService.orderDetail(orderNumber);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "status", "COMPLETED",
                        "order", order,
                        "message", "Đơn hàng đã được tạo"
                ));
            } catch (Exception e) {
                log.error("Error getting order details: ", e);
            }
        }

        if (checkoutController.getCheckoutSessions().containsKey(sessionId)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                    "success", false,
                    "status", "PENDING",
                    "message", "Đang chờ xác nhận từ MoMo..."
            ));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "status", "NOT_FOUND",
                "message", "Session không tồn tại hoặc đã hết hạn"
        ));
    }

    private Integer parseResultCode(Object resultCodeObj) {
        if (resultCodeObj == null) return -1;
        try {
            if (resultCodeObj instanceof Number) {
                return ((Number) resultCodeObj).intValue();
            } else {
                return Integer.parseInt(String.valueOf(resultCodeObj));
            }
        } catch (Exception e) {
            return -1;
        }
    }

    private void processSuccessfulPayment(String sessionId, Map<String, Object> callbackData) {
        if (completedOrders.containsKey(sessionId)) {
            return; // Already processed
        }

        CheckoutRequest checkoutRequest = checkoutController.getCheckoutSessions().get(sessionId);
        String username = checkoutController.getSessionUsers().get(sessionId);

        if (checkoutRequest != null && username != null) {
            synchronized (sessionId.intern()) {
                if (completedOrders.containsKey(sessionId)) {
                    return; // Double-check
                }

                try {
                    User user = userService.findByUsername(username);
                    OrderResponse order = orderService.createOrder(user, checkoutRequest);

                    // Update payment status
                    String transId = String.valueOf(callbackData.get("transId"));
                    try {
                        orderService.updatePaymentStatus(order.getOrderNumber(), "COMPLETED", transId);
                    } catch (Exception e) {
                        log.error("Failed to update payment status: ", e);
                    }

                    // Save completed order
                    completedOrders.put(sessionId, order.getOrderNumber());

                    // Cleanup
                    checkoutController.getCheckoutSessions().remove(sessionId);
                    checkoutController.getSessionUsers().remove(sessionId);
                } catch (Exception e) {
                    log.error("Error creating order: ", e);
                }
            }
        }
    }

    private ResponseEntity<?> handleCheckoutReturn(String orderId) throws InterruptedException {
        String orderNumber = completedOrders.get(orderId);

        if (orderNumber != null) {
            OrderResponse order = orderService.orderDetail(orderNumber);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "status", "COMPLETED",
                    "message", "Thanh toán thành công",
                    "order", order
            ));
        }

        // Wait for IPN processing
        for (int i = 0; i < 20; i++) {
            Thread.sleep(500);
            orderNumber = completedOrders.get(orderId);
            if (orderNumber != null) {
                OrderResponse order = orderService.orderDetail(orderNumber);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "status", "COMPLETED",
                        "message", "Thanh toán thành công",
                        "order", order
                ));
            }
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "success", false,
                "status", "PENDING",
                "message", "Đang xử lý thanh toán. Vui lòng đợi...",
                "sessionId", orderId,
                "pending", true
        ));
    }

    private void cleanupSession(String sessionId) {
        if (sessionId != null && sessionId.startsWith("CHECKOUT_")) {
            checkoutController.getCheckoutSessions().remove(sessionId);
            checkoutController.getSessionUsers().remove(sessionId);
        }
    }
}