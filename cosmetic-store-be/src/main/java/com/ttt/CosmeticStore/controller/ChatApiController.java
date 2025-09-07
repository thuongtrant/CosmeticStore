package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.request.CustomerMessRequest;
import com.ttt.CosmeticStore.dto.response.ChatInitResponse;
import com.ttt.CosmeticStore.service.ChatService;
import com.ttt.CosmeticStore.service.UserAuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@PreAuthorize("hasRole('CUSTOMER')")
public class ChatApiController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserAuthenticationService userAuthService;

    @PostMapping("/init")
    public ResponseEntity<ChatInitResponse> initializeChat(
            @Valid @RequestBody CustomerMessRequest request,
            Authentication authentication) {

        try {
            // Validation
            if (!userAuthService.isValidUser(authentication)) {
                return ResponseEntity.badRequest()
                        .body(ChatInitResponse.error("Không tìm thấy thông tin người dùng"));
            }

            // Get user info
            Long customerId = userAuthService.getCurrentUserId(authentication);
            String customerName = userAuthService.getCustomerName(authentication, request.getCustomerName());

            // Create or get chat room
            var chatRoom = chatService.createOrGetChatRoom(customerId, customerName);

            return ResponseEntity.ok(new ChatInitResponse(chatRoom));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ChatInitResponse.error("Lỗi khởi tạo chat: " + e.getMessage()));
        }
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Tin nhắn không được để trống");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate user
            if (!userAuthService.isValidUser(authentication)) {
                response.put("success", false);
                response.put("message", "Người dùng không hợp lệ");
                return ResponseEntity.badRequest().body(response);
            }

            // This endpoint is just for validation - actual message sending is handled by Firebase
            response.put("success", true);
            response.put("message", "Tin nhắn đã được gửi");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi gửi tin nhắn: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/validate-user")
    public ResponseEntity<Map<String, Object>> validateUser(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (userAuthService.isValidUser(authentication)) {
                response.put("success", true);
                response.put("message", "Người dùng hợp lệ");
                response.put("userId", authentication.getName());
            } else {
                response.put("success", false);
                response.put("message", "Người dùng chưa đăng nhập");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi xác thực: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}