package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.entity.Order;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.service.InventoryService;
import com.ttt.CosmeticStore.service.OrderService;
import com.ttt.CosmeticStore.service.PaymentSessionService;
import com.ttt.CosmeticStore.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    private PaymentSessionService sessionService;

    @Autowired
    private InventoryService inventoryService;

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
                sessionService.cleanupSession(sessionId);
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
            log.info("MoMo return - OrderId: {}, ResultCode: {}, Message: {}", orderId, resultCode, message);

            if (resultCode == 0) {
                // Success case
                if (orderId.startsWith("CHECKOUT_")) {
                    return handleCheckoutReturn(orderId);
                }

                OrderResponse order = orderService.orderDetail(orderId);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "status", "COMPLETED",
                        "message", "Thanh toán thành công",
                        "order", order
                ));
            }
            else if (resultCode == 1006) {
                // Transaction is being processed
                if (orderId.startsWith("CHECKOUT_")) {
                    return ResponseEntity.ok(Map.of(
                            "success", false,
                            "status", "PROCESSING",
                            "message", "Giao dịch đang được xử lý. Vui lòng đợi...",
                            "sessionId", orderId,
                            "shouldPoll", true
                    ));
                } else {
                    // Direct order - also return processing status
                    return ResponseEntity.ok(Map.of(
                            "success", false,
                            "status", "PROCESSING",
                            "message", "Giao dịch đang được xử lý",
                            "orderId", orderId,
                            "shouldPoll", true
                    ));
                }
            }
            else {
                // True failure cases
                sessionService.cleanupSession(orderId);
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "status", "FAILED",
                        "message", "Thanh toán thất bại: " + (message != null ? message : "Unknown error"),
                        "redirectToCheckout", true
                ));
            }

        } catch (Exception e) {
            log.error("Error handling MoMo return: ", e);
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

        // Check if order is completed
        String orderNumber = sessionService.getCompletedOrder(sessionId);
        if (orderNumber != null) {
            try {
                OrderResponse order = orderService.orderDetail(orderNumber);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "status", "COMPLETED",
                        "order", order,
                        "message", "Đơn hàng đã được tạo thành công"
                ));
            } catch (Exception e) {
                log.error("Error getting order details: ", e);
            }
        }

        // Check if session still active
        if (sessionService.hasActiveSession(sessionId)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                    "success", false,
                    "status", "PENDING",
                    "message", "Đang chờ xác nhận từ MoMo...",
                    "shouldContinuePolling", true
            ));
        }

        // Session not found - might be expired or cleaned up due to failure
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "status", "EXPIRED",
                "message", "Session đã hết hạn. Vui lòng thực hiện lại giao dịch",
                "redirectToCheckout", true
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
        String existingOrder = sessionService.getCompletedOrder(sessionId);
        if (existingOrder != null) {
            return; // Already processed
        }

        CheckoutRequest checkoutRequest = sessionService.getCheckoutRequest(sessionId);
        String username = sessionService.getSessionUser(sessionId);

        if (checkoutRequest != null && username != null) {
            synchronized (sessionId.intern()) {
                if (sessionService.getCompletedOrder(sessionId) != null) {
                    return; // Double-check
                }

                try {
                    User user = userService.findByUsername(username);
                    OrderResponse order = orderService.createOrder(user, checkoutRequest);

                    // Trừ tồn kho khi thanh toán MoMo thành công
                    Order orderEntity = orderService.getOrderEntity(order.getOrderNumber());
                    inventoryService.confirmInventoryDeduction(orderEntity);

                    // Update payment status
                    String transId = String.valueOf(callbackData.get("transId"));
                    try {
                        orderService.updatePaymentStatus(order.getOrderNumber(), "COMPLETED", transId);
                    } catch (Exception e) {
                        log.error("Failed to update payment status: ", e);
                    }

                    // Save completed order and cleanup session
                    sessionService.markOrderCompleted(sessionId, order.getOrderNumber());
                    sessionService.cleanupSession(sessionId);
                } catch (Exception e) {
                    log.error("Error creating order: ", e);
                }
            }
        }
    }

    private ResponseEntity<?> handleCheckoutReturn(String orderId) throws InterruptedException {
        String orderNumber = sessionService.getCompletedOrder(orderId);

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
            orderNumber = sessionService.getCompletedOrder(orderId);
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
}