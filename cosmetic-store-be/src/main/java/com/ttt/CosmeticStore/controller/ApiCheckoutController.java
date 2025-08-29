
package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.dto.response.MoMoResponse;
import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.service.MoMoService;
import com.ttt.CosmeticStore.service.OrderService;
import com.ttt.CosmeticStore.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

        import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/checkout")
public class ApiCheckoutController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MoMoService moMoService;

    @Autowired
    private UserService userService;

    private final Map<String, CheckoutRequest> checkoutSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionUsers = new ConcurrentHashMap<>();

    @PostMapping("/orders")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());

            if ("MOMO".equals(request.getPaymentMethod())) {
                return processMoMoPayment(request, user);
            } else {
                return processCODPayment(request, user);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Đặt hàng thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    private ResponseEntity<?> processMoMoPayment(CheckoutRequest request, User user) throws Exception {
        BigDecimal tempTotal = orderService.calculateTotalAmount(request);
        long amount = tempTotal.longValue();

        // Validate amount
        if (amount < 10000 || amount > 50000000) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", amount < 10000 ?
                    "Số tiền tối thiểu cho MoMo là 10,000đ" :
                    "Số tiền tối đa cho MoMo là 50,000,000đ");
            response.put("suggestCOD", true);
            return ResponseEntity.badRequest().body(response);
        }

        // Create session
        String sessionId = "CHECKOUT_" + System.currentTimeMillis();
        checkoutSessions.put(sessionId, request);
        sessionUsers.put(sessionId, user.getUsername());

        // Create MoMo payment
        MoMoResponse momoResponse = moMoService.createPayment(
                sessionId,
                amount,
                "Thanh toán đơn hàng",
                request.getMomoRequestType() != null ? request.getMomoRequestType() : "captureWallet"
        );

        if (momoResponse == null) {
            throw new RuntimeException("Không nhận được phản hồi từ MoMo");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("sessionId", sessionId);

        // Handle different response types
        if (momoResponse.getPayUrl() != null) {
            response.put("payUrl", momoResponse.getPayUrl());
            response.put("message", "Đang chuyển đến trang thanh toán MoMo...");
        } else if (momoResponse.getQrCodeUrl() != null) {
            response.put("qrCodeUrl", momoResponse.getQrCodeUrl());
            response.put("message", "Vui lòng quét mã QR để thanh toán");
        } else if (momoResponse.getDeeplink() != null) {
            response.put("deeplink", momoResponse.getDeeplink());
            response.put("message", "Mở ứng dụng MoMo để thanh toán");
        } else {
            throw new RuntimeException("MoMo không trả về link thanh toán");
        }

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> processCODPayment(CheckoutRequest request, User user) throws Exception {
        OrderResponse order = orderService.createOrder(user, request);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đặt hàng thành công");
        response.put("order", order);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            List<OrderResponse> orders = orderService.getMyOrders(user);
            return ResponseEntity.ok(Map.of("success", true, "orders", orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Lỗi lấy danh sách đơn hàng: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/orders/{orderNumber}")
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderNumber) {
        try {
            OrderResponse order = orderService.orderDetail(orderNumber);
            return ResponseEntity.ok(Map.of("success", true, "order", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Không tìm thấy đơn hàng: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/methods")
    public ResponseEntity<?> getPaymentMethods() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "methods", List.of(
                        Map.of("id", "COD", "name", "COD", "description", "Thanh toán khi nhận hàng"),
                        Map.of("id", "MOMO", "name", "Ví MoMo", "description", "Thanh toán qua ví điện tử MoMo")
                )
        ));
    }

    // Getter methods for MoMoController to access shared sessions
    public Map<String, CheckoutRequest> getCheckoutSessions() {
        return checkoutSessions;
    }

    public Map<String, String> getSessionUsers() {
        return sessionUsers;
    }
}