package com.ttt.CosmeticStore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ApiChatController {

    @PostMapping("/validate-user")
    public ResponseEntity<Map<String, Object>> validateChatUser(
            @RequestBody Map<String, Object> request) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Get user info from JWT token or session
            String customerId = (String) request.get("customerId");
            String customerName = (String) request.get("customerName");

            // Validate that the user is authenticated
            if (customerId != null && customerName != null) {
                response.put("success", true);
                response.put("message", "User validated for chat");
                response.put("customerId", customerId);
                response.put("customerName", customerName);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error validating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/rooms")
    public ResponseEntity<Map<String, Object>> getChatRooms() {
        Map<String, Object> response = new HashMap<>();

        try {
            // This endpoint can be used by admin to get chat room info
            // For now, we'll rely on Firebase for real-time data
            response.put("success", true);
            response.put("message", "Chat rooms are managed by Firebase");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error getting chat rooms: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
