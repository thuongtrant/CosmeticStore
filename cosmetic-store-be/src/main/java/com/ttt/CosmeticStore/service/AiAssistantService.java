package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.AiChatRequest;
import com.ttt.CosmeticStore.dto.response.AiChatResponse;

public interface AiAssistantService {
    AiChatResponse processUserMessage(AiChatRequest request);
    String generateProductRecommendation(String skinType, String concern);
    boolean isAiEnabled();
}
