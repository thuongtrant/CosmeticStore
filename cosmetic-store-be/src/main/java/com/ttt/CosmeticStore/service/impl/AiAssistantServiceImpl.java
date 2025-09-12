package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.request.AiChatRequest;
import com.ttt.CosmeticStore.dto.response.AiChatResponse;
import com.ttt.CosmeticStore.entity.Product;
import com.ttt.CosmeticStore.mapper.AiDataMapper;
import com.ttt.CosmeticStore.repository.ProductRepository;
import com.ttt.CosmeticStore.service.AiAssistantService;
import com.ttt.CosmeticStore.service.AiRateLimitService;
import com.ttt.CosmeticStore.service.OpenAIAssistantApiService;
import com.ttt.CosmeticStore.service.ThreadManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class AiAssistantServiceImpl implements AiAssistantService {

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.assistant.id:}")
    private String assistantId;

    @Value("${openai.assistant.enabled:false}")
    private boolean aiEnabled;

    @Value("${openai.assistant.timeout-seconds:30}")
    private int timeoutSeconds;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AiDataMapper aiDataMapper;


    @Autowired
    private AiRateLimitService aiRateLimitService;

    @Autowired
    private OpenAIAssistantApiService openAIAssistantApiService;

    @Autowired
    private ThreadManagerService threadManagerService;

    private volatile boolean aiServiceHealthy = true;
    private volatile LocalDateTime lastHealthCheck = LocalDateTime.now();

    @Override
    public AiChatResponse processUserMessage(AiChatRequest request) {
        // Check AI health status
        if (!isAiEnabled()) {
            return AiChatResponse.error("AI Assistant hiện đang bảo trì...");
        }

        // Rate limiting check
        if (!aiRateLimitService.checkAndUpdateRateLimit(request.getUserId())) {
            int remainingMessages = aiRateLimitService.getRemainingMessages(request.getUserId());
            LocalDateTime resetTime = aiRateLimitService.getResetTime(request.getUserId());

            return AiChatResponse.rateLimitError(String.format(
                    "Bạn đã gửi quá nhiều tin nhắn. Vui lòng chờ %d phút nữa để tiếp tục. " +
                            "Hiện tại bạn còn %d tin nhắn có thể gửi.",
                    java.time.Duration.between(LocalDateTime.now(), resetTime).toMinutes() + 1,
                    remainingMessages
            ), remainingMessages, resetTime);
        }

        String sessionId = request.getChatRoomId() != null ? request.getChatRoomId() :
                "ai_session_" + request.getUserId() + "_" + System.currentTimeMillis();

        try {


            String threadId = threadManagerService.getOrCreateThreadForUser(request.getUserId());

            //Gửi message lên OpenAI Assistant
            String aiResponse = openAIAssistantApiService.processMessageWithAssistant(
                    threadId, request.getMessage(), assistantId);

            //Build response with recommendations
            AiChatResponse response = AiChatResponse.success(aiResponse);
            response.setChatRoomId(sessionId);

            // Extract product recommendations - keep this for UI compatibility
            List<Product> allProducts = productRepository.findAll();
            List<AiChatResponse.ProductRecommendation> recommendations =
                    aiDataMapper.extractProductRecommendations(aiResponse, allProducts);
            response.setRecommendations(recommendations);

            // Mark AI service as healthy
            markAiServiceHealthy();

            log.info("Successfully processed AI message for userId: {}, sessionId: {}, threadId: {}",
                    request.getUserId(), sessionId, threadId);

            return response;

        } catch (Exception e) {
            log.error("Error processing AI message for userId: {}", request.getUserId(), e);

            // Mark AI service as potentially unhealthy
            markAiServiceUnhealthy();

            // Determine error type and return appropriate message
            if (e.getMessage().contains("timeout") || e.getMessage().contains("connect")) {
                return AiChatResponse.error("AI Assistant đang quá tải. Vui lòng thử lại sau ít phút hoặc chat với nhân viên tư vấn.");
            } else {
                return AiChatResponse.error("Có lỗi xảy ra khi xử lý tin nhắn. Vui lòng thử lại hoặc liên hệ nhân viên tư vấn.");
            }
        }
    }

    @Override
    public String generateProductRecommendation(String skinType, String concern) {
        if (!aiEnabled || !aiServiceHealthy) {
            return "AI Assistant hiện không khả dụng. Vui lòng chat trực tiếp với nhân viên tư vấn.";
        }

        try {
            // Create a temporary thread for this standalone recommendation
            Long tempUserId = System.currentTimeMillis(); // Use timestamp as temp user ID
            String threadId = threadManagerService.createNewThreadForUser(tempUserId);

            String prompt = String.format(
                    "Khách hàng có loại da '%s' và quan tâm về '%s'. Hãy gợi ý 2-3 sản phẩm phù hợp và giải thích tại sao chọn những sản phẩm đó.",
                    skinType, concern
            );

            String response = openAIAssistantApiService.processMessageWithAssistant(
                    threadId, prompt, assistantId);

            threadManagerService.clearThreadForUser(tempUserId);

            markAiServiceHealthy();
            return response;
        } catch (Exception e) {
            log.error("Error generating product recommendation", e);
            markAiServiceUnhealthy();
            return "Không thể tạo gợi ý sản phẩm lúc này. Vui lòng thử lại sau.";
        }
    }
    @Override
    public boolean isAiEnabled() {
        return aiEnabled &&
                openaiApiKey != null &&
                !openaiApiKey.isEmpty() &&
                assistantId != null &&
                !assistantId.isEmpty() &&
                aiServiceHealthy;
    }

    private void markAiServiceHealthy() {
        aiServiceHealthy = true;
        lastHealthCheck = LocalDateTime.now();
    }

    private void markAiServiceUnhealthy() {
        aiServiceHealthy = false;
        lastHealthCheck = LocalDateTime.now();
    }
}