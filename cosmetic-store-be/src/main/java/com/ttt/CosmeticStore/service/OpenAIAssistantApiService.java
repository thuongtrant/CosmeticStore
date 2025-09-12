package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.openai.*;

public interface OpenAIAssistantApiService {

    /**
     * Create a new thread for conversation
     */
    OpenAIThreadResponse createThread(Long userId);

    /**
     * Add a message to a thread
     */
    OpenAIMessageResponse addMessageToThread(String threadId, String message);

    /**
     * Run the assistant on a thread
     */
    OpenAIRunResponse runAssistant(String threadId, String assistantId);

    /**
     * Get the status of a run
     */
    OpenAIRunResponse getRunStatus(String threadId, String runId);

    /**
     * Get messages from a thread
     */
    OpenAIMessagesResponse getMessages(String threadId);

    /**
     * Get the latest assistant response from a thread
     */
    String getLatestAssistantResponse(String threadId);

    /**
     * Process a complete message cycle: add message -> run assistant -> get response
     */
    String processMessageWithAssistant(String threadId, String message, String assistantId);
}
