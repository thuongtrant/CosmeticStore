package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.request.AiChatRequest;
import com.ttt.CosmeticStore.dto.response.AiChatResponse;
import com.ttt.CosmeticStore.service.AiAssistantService;
import com.ttt.CosmeticStore.service.ThreadManagerService;
import com.ttt.CosmeticStore.service.UserAuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai-chat")
public class AiChatController {

    @Autowired
    private AiAssistantService aiAssistantService;

    @Autowired
    private ThreadManagerService threadManagerService;

    @Autowired
    private UserAuthenticationService userAuthService;

    @PostMapping("/message")
    public ResponseEntity<AiChatResponse> sendMessage(
            @Valid @RequestBody AiChatRequest request,
            Authentication authentication) {

        try {
            if (!userAuthService.isValidUser(authentication)) {
                return ResponseEntity.badRequest()
                        .body(AiChatResponse.error("Không tìm thấy thông tin người dùng"));
            }

            Long userId = userAuthService.getCurrentUserId(authentication);
            request.setUserId(userId);

            if (!aiAssistantService.isAiEnabled()) {
                return ResponseEntity.ok(AiChatResponse.error(
                    "Xin chào! Hiện tại AI Assistant đang được bảo trì. " +
                    "Bạn có thể chat trực tiếp với nhân viên tư vấn của chúng tôi."
                ));
            }
            AiChatResponse response = aiAssistantService.processUserMessage(request);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(AiChatResponse.error("Lỗi xử lý tin nhắn: " + e.getMessage()));
        }
    }

    @GetMapping("/recommendation")
    public ResponseEntity<String> getRecommendation(
            @RequestParam String skinType,
            @RequestParam String concern,
            Authentication authentication) {

        try {
            if (!userAuthService.isValidUser(authentication)) {
                return ResponseEntity.badRequest()
                        .body("Không tìm thấy thông tin người dùng");
            }

            if (!aiAssistantService.isAiEnabled()) {
                return ResponseEntity.ok(
                    "AI Assistant hiện đang bảo trì. Vui lòng chat trực tiếp với nhân viên tư vấn."
                );
            }

            String recommendation = aiAssistantService.generateProductRecommendation(skinType, concern);
            return ResponseEntity.ok(recommendation);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Lỗi tạo gợi ý: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> getAiStatus() {
        return ResponseEntity.ok(aiAssistantService.isAiEnabled());
    }

    @PostMapping("/new-conversation")
    public ResponseEntity<AiChatResponse> startNewConversation(Authentication authentication) {
        try {
            if (!userAuthService.isValidUser(authentication)) {
                return ResponseEntity.badRequest()
                        .body(AiChatResponse.error("Không tìm thấy thông tin người dùng"));
            }

            Long userId = userAuthService.getCurrentUserId(authentication);

            threadManagerService.clearThreadForUser(userId);

            String welcomeMessage = """
                👋 **Xin chào! Tôi là AI Assistant chuyên tư vấn skincare của BeautyForYou.**
                
                Để tư vấn chính xác nhất, bạn có thể cho tôi biết:
                🌟 **Loại da của bạn** (da dầu, da khô, da hỗn hợp, da nhạy cảm, da thường)
                🎯 **Mối quan tâm chính** (mụn, lão hóa, thâm nám, dưỡng ẩm, chống nắng...)
                
                Tôi sẽ gợi ý những sản phẩm phù hợp nhất cho bạn! 💄✨
                """;

            AiChatResponse response = AiChatResponse.success(welcomeMessage);
            response.setChatRoomId("ai_session_new_" + userId + "_" + System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(AiChatResponse.error("Lỗi tạo cuộc trò chuyện mới: " + e.getMessage()));
        }
    }

    @GetMapping("/quick-responses")
    public ResponseEntity<Map<String, String>> getQuickResponses() {
        Map<String, String> quickResponses = Map.of(
            "da_dau", "Tôi có da dầu và muốn tìm sản phẩm phù hợp",
            "da_kho", "Tôi có da khô và cần dưỡng ẩm",
            "da_hon_hop", "Tôi có da hỗn hợp, vùng T nhờn, má khô",
            "da_nhay_cam", "Tôi có da nhạy cảm, dễ kích ứng",
            "mun", "Tôi đang gặp vấn đề về mụn", "chua_biet", "Tôi chưa biết loại da của mình, hướng dẫn giúp tôi"

                );

        return ResponseEntity.ok(quickResponses);
    }
}
