package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.dto.response.OrdersResponseC;
import com.ttt.CosmeticStore.dto.response.PagedResponse;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.service.OrderService;
import com.ttt.CosmeticStore.service.PaymentProcessingService;
import com.ttt.CosmeticStore.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/checkout")
public class ApiCheckoutController {

    @Autowired
    private PaymentProcessingService paymentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/orders")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());

            if ("MOMO".equals(request.getPaymentMethod())) {
                return paymentService.processMoMoPayment(request, user);
            } else {
                return paymentService.processCODPayment(request, user);
            }
        } catch (Exception e) {
            log.error("Checkout error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("success", false, "message", "Lỗi hệ thống")
            );
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<PagedResponse<OrdersResponseC>> getUserOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            PagedResponse<OrdersResponseC> response = orderService.getMyOrders(user, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
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
}