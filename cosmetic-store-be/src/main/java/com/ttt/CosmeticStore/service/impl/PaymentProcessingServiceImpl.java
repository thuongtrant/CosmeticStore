package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.dto.response.MoMoResponse;
import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.exception.OrderException;
import com.ttt.CosmeticStore.service.MoMoService;
import com.ttt.CosmeticStore.service.OrderService;
import com.ttt.CosmeticStore.service.PaymentProcessingService;
import com.ttt.CosmeticStore.service.PaymentSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PaymentProcessingServiceImpl implements PaymentProcessingService {

    @Autowired
    private MoMoService moMoService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentSessionService sessionService;

    public ResponseEntity<?> processMoMoPayment(CheckoutRequest request, User user) {
        try {
            // Validate amount
            BigDecimal total = orderService.calculateTotalAmount(request);
            long amount = total.longValue();

            if (!isValidMoMoAmount(amount)) {
                return createAmountErrorResponse(amount);
            }

            // Create session
            String sessionId = sessionService.createSession(request, user.getUsername());

            // Create MoMo payment
            MoMoResponse momoResponse = moMoService.createPayment(
                    sessionId, amount, "Thanh toán đơn hàng",
                    request.getMomoRequestType() != null ? request.getMomoRequestType() : "captureWallet"
            );

            return buildMoMoResponse(sessionId, momoResponse);

        } catch (OrderException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        } catch (Exception e) {
            log.error("MoMo payment processing error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("success", false, "message", "Lỗi xử lý thanh toán")
            );
        }
    }

    public ResponseEntity<?> processCODPayment(CheckoutRequest request, User user) {
        try {
            OrderResponse order = orderService.createOrder(user, request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đặt hàng thành công",
                    "order", order
            ));
        } catch (OrderException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }

    private boolean isValidMoMoAmount(long amount) {
        return amount >= 10000 && amount <= 50000000;
    }

    private ResponseEntity<?> createAmountErrorResponse(long amount) {
        String message = amount < 10000 ?
                "Số tiền tối thiểu cho MoMo là 10,000đ" :
                "Số tiền tối đa cho MoMo là 50,000,000đ";

        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", message,
                "suggestCOD", true
        ));
    }

    private ResponseEntity<?> buildMoMoResponse(String sessionId, MoMoResponse momoResponse) {
        if (momoResponse == null) {
            throw new RuntimeException("Không nhận được phản hồi từ MoMo");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("sessionId", sessionId);

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
}

