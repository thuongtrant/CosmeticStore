package com.ttt.CosmeticStore.controller.admin;

import com.ttt.CosmeticStore.dto.response.ChatRoomResponse;
import com.ttt.CosmeticStore.exception.ChatRoomNotFoundException;
import com.ttt.CosmeticStore.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/chat")
@PreAuthorize("hasRole('ADMIN')")
public class AdminChatApiController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/rooms")
    public ResponseEntity<Map<String, Object>> getChatRooms() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ChatRoomResponse> chatRooms = chatService.getAllActiveChatRooms();
            response.put("success", true);
            response.put("data", chatRooms);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tải danh sách chat: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/rooms/{chatRoomId}/mark-read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long chatRoomId) {
        Map<String, Object> response = new HashMap<>();
        try {
            chatService.markChatRoomAsRead(chatRoomId);
            response.put("success", true);
            response.put("message", "Đã đánh dấu đã đọc");
            return ResponseEntity.ok(response);
        } catch (ChatRoomNotFoundException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
