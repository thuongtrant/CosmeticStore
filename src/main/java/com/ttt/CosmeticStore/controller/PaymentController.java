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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
@Slf4j
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

//    @PostMapping("/process/{orderNumber}")
//    public ResponseEntity<?> processPayment(
//            @PathVariable String orderNumber,
//            @RequestParam String paymentMethod) {
//        try {
//            OrderResponse order = orderService.processPayment(orderNumber, paymentMethod);
//
//            Map<String, Object> response = new HashMap<>();
//            if ("COMPLETED".equals(order.getPayment().getStatus())) {
//                response.put("success", true);
//                response.put("message", "Thanh toán thành công");
//            } else {
//                response.put("success", false);
//                response.put("message", "Thanh toán thất bại");
//            }
//            response.put("order", order);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", "Xử lý thanh toán thất bại: " + e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//    }

    @Autowired
    private MoMoService moMoService;

    @PostMapping("/process/{orderNumber}")
    public ResponseEntity<?> processPayment(
            @PathVariable String orderNumber,
            @RequestParam String paymentMethod) {
        try {
            // Nếu là thanh toán MoMo, tạo URL thanh toán
            if ("MOMO".equals(paymentMethod)) {
                OrderResponse order = orderService.getOrderByNumber(orderNumber);

                MoMoResponse momoResponse = moMoService.createPayment(
                        orderNumber,
                        order.getTotalAmount().longValue(),
                        "Thanh toán đơn hàng " + orderNumber
                );

                Map<String, Object> response = new HashMap<>();
                if (momoResponse.getResultCode() == 0) {
                    response.put("success", true);
                    response.put("message", "Tạo liên kết thanh toán MoMo thành công");
                    response.put("payUrl", momoResponse.getPayUrl());
                    response.put("qrCodeUrl", momoResponse.getQrCodeUrl());
                    response.put("deeplink", momoResponse.getDeeplink());
                } else {
                    response.put("success", false);
                    response.put("message", "Tạo thanh toán MoMo thất bại: " + momoResponse.getMessage());
                }
                response.put("order", order);

                return ResponseEntity.ok(response);
            } else {
                // Xử lý các phương thức thanh toán khác
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
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Xử lý thanh toán thất bại: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Thêm endpoint xử lý callback từ MoMo
    @PostMapping("/momo/callback")
    public ResponseEntity<?> handleMoMoCallback(@RequestBody Map<String, Object> callbackData) {
        try {
            String orderId = (String) callbackData.get("orderId");
            Integer resultCode = (Integer) callbackData.get("resultCode");
            String signature = (String) callbackData.get("signature");

            // Tạo raw data để verify signature
            String rawData = "accessKey=" + callbackData.get("accessKey") +
                    "&amount=" + callbackData.get("amount") +
                    "&extraData=" + callbackData.get("extraData") +
                    "&message=" + callbackData.get("message") +
                    "&orderId=" + orderId +
                    "&orderInfo=" + callbackData.get("orderInfo") +
                    "&orderType=" + callbackData.get("orderType") +
                    "&partnerCode=" + callbackData.get("partnerCode") +
                    "&payType=" + callbackData.get("payType") +
                    "&requestId=" + callbackData.get("requestId") +
                    "&responseTime=" + callbackData.get("responseTime") +
                    "&resultCode=" + resultCode +
                    "&transId=" + callbackData.get("transId");

            // Verify signature
            if (!moMoService.verifySignature(signature, rawData)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid signature"));
            }

            // Cập nhật trạng thái thanh toán
            if (resultCode == 0) {
                // Thanh toán thành công
                orderService.updatePaymentStatus(orderId, "COMPLETED", (String) callbackData.get("transId"));
            } else {
                // Thanh toán thất bại
                orderService.updatePaymentStatus(orderId, "FAILED", null);
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Lỗi xử lý callback MoMo: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint xử lý khi user quay lại từ MoMo
    @GetMapping("/momo/return")
    public ResponseEntity<?> handleMoMoReturn(
            @RequestParam String orderId,
            @RequestParam Integer resultCode,
            @RequestParam(required = false) String message) {
        try {
            Map<String, Object> response = new HashMap<>();

            if (resultCode == 0) {
                OrderResponse order = orderService.getOrderByNumber(orderId);
                response.put("success", true);
                response.put("message", "Thanh toán MoMo thành công");
                response.put("order", order);
            } else {
                response.put("success", false);
                response.put("message", "Thanh toán MoMo thất bại: " + message);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Xử lý kết quả thanh toán thất bại: " + e.getMessage());

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
            Map.of("id", "COD", "name", "COD", "description", "Thanh toán khi nhận hàng"),
//            Map.of("id", "BANK_TRANSFER", "name", "Chuyển khoản ngân hàng", "description", "Chuyển khoản qua tài khoản ngân hàng"),
            Map.of("id", "MOMO", "name", "Ví MoMo", "description", "Thanh toán qua ví điện tử MoMo")
//            Map.of("id", "VNPAY", "name", "VNPay", "description", "Thanh toán qua cổng VNPay")
        ));

        return ResponseEntity.ok(response);
    }
}
