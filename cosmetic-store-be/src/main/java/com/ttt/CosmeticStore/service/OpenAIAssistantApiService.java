package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.openai.*;

public interface OpenAIAssistantApiService {

    OpenAIThreadResponse createThread(Long userId);
    OpenAIMessageResponse addMessageToThread(String threadId, String message);
    OpenAIRunResponse runAssistant(String threadId, String assistantId);
    OpenAIRunResponse getRunStatus(String threadId, String runId);
    OpenAIMessagesResponse getMessages(String threadId);
    String getLatestAssistantResponse(String threadId);
    String processMessageWithAssistant(String threadId, String message, String assistantId);
}
