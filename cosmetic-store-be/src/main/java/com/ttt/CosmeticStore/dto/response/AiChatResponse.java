package com.ttt.CosmeticStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponse {
    private String message;
    private boolean success;
    private String error;
    private List<ProductRecommendation> recommendations;
    private LocalDateTime timestamp;
    private String chatRoomId;

    // Rate limiting information
    private Integer remainingMessages;
    private LocalDateTime rateLimitResetTime;
    private boolean rateLimited;

    public AiChatResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.timestamp = LocalDateTime.now();
    }

    public static AiChatResponse success(String message) {
        AiChatResponse response = new AiChatResponse();
        response.setMessage(message);
        response.setSuccess(true);
        response.setTimestamp(LocalDateTime.now());
        response.setRateLimited(false);
        return response;
    }

    public static AiChatResponse error(String error) {
        AiChatResponse response = new AiChatResponse();
        response.setSuccess(false);
        response.setError(error);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public static AiChatResponse rateLimitError(String error, int remainingMessages, LocalDateTime resetTime) {
        AiChatResponse response = new AiChatResponse();
        response.setSuccess(false);
        response.setError(error);
        response.setTimestamp(LocalDateTime.now());
        response.setRateLimited(true);
        response.setRemainingMessages(remainingMessages);
        response.setRateLimitResetTime(resetTime);
        return response;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductRecommendation {
        private Long productId;
        private String productName;
        private String reason;
        private Double price;
        private String imageUrl;
    }
}
