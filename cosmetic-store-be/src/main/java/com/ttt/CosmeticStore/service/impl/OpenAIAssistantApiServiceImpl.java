package com.ttt.CosmeticStore.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttt.CosmeticStore.dto.openai.*;
import com.ttt.CosmeticStore.service.OpenAIAssistantApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OpenAIAssistantApiServiceImpl implements OpenAIAssistantApiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.assistant.timeout-seconds:30}")
    private int timeoutSeconds;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String BASE_URL = "https://api.openai.com/v1";
    private static final String THREADS_URL = BASE_URL + "/threads";
    private static final String BETA_HEADER = "assistants=v2";

    public OpenAIAssistantApiServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public OpenAIThreadResponse createThread(Long userId) {
        try {
            HttpHeaders headers = createHeaders();

            OpenAIThreadRequest request = new OpenAIThreadRequest();
            Map<String, String> metadata = new HashMap<>();
            metadata.put("user_id", userId.toString());
            metadata.put("created_at", String.valueOf(System.currentTimeMillis()));
            request.setMetadata(metadata);

            HttpEntity<OpenAIThreadRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<OpenAIThreadResponse> response = restTemplate.exchange(
                THREADS_URL, HttpMethod.POST, entity, OpenAIThreadResponse.class
            );

            log.info("Created new thread {} for user {}", response.getBody().getId(), userId);
            return response.getBody();

        } catch (Exception e) {
            log.error("Error creating thread for user {}", userId, e);
            throw new RuntimeException("Failed to create conversation thread", e);
        }
    }

    @Override
    public OpenAIMessageResponse addMessageToThread(String threadId, String message) {
        try {
            HttpHeaders headers = createHeaders();

            OpenAIMessageRequest request = OpenAIMessageRequest.userMessage(message);

            HttpEntity<OpenAIMessageRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<OpenAIMessageResponse> response = restTemplate.exchange(
                THREADS_URL + "/" + threadId + "/messages",
                HttpMethod.POST, entity, OpenAIMessageResponse.class
            );

            log.debug("Added message to thread {}", threadId);
            return response.getBody();

        } catch (Exception e) {
            log.error("Error adding message to thread {}", threadId, e);
            throw new RuntimeException("Failed to add message to thread", e);
        }
    }

    @Override
    public OpenAIRunResponse runAssistant(String threadId, String assistantId) {
        try {
            HttpHeaders headers = createHeaders();

            OpenAIRunRequest request = OpenAIRunRequest.withAssistant(assistantId);

            HttpEntity<OpenAIRunRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<OpenAIRunResponse> response = restTemplate.exchange(
                THREADS_URL + "/" + threadId + "/runs",
                HttpMethod.POST, entity, OpenAIRunResponse.class
            );

            log.debug("Started run {} for thread {} with assistant {}",
                response.getBody().getId(), threadId, assistantId);
            return response.getBody();

        } catch (Exception e) {
            log.error("Error running assistant on thread {}", threadId, e);
            throw new RuntimeException("Failed to run assistant", e);
        }
    }

    @Override
    public OpenAIRunResponse getRunStatus(String threadId, String runId) {
        try {
            HttpHeaders headers = createHeaders();

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<OpenAIRunResponse> response = restTemplate.exchange(
                THREADS_URL + "/" + threadId + "/runs/" + runId,
                HttpMethod.GET, entity, OpenAIRunResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error getting run status for thread {} run {}", threadId, runId, e);
            throw new RuntimeException("Failed to get run status", e);
        }
    }

    @Override
    public OpenAIMessagesResponse getMessages(String threadId) {
        try {
            HttpHeaders headers = createHeaders();

            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<OpenAIMessagesResponse> response = restTemplate.exchange(
                THREADS_URL + "/" + threadId + "/messages?order=desc&limit=10",
                HttpMethod.GET, entity, OpenAIMessagesResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Error getting messages from thread {}", threadId, e);
            throw new RuntimeException("Failed to get messages", e);
        }
    }

    @Override
    public String getLatestAssistantResponse(String threadId) {
        try {
            OpenAIMessagesResponse messagesResponse = getMessages(threadId);

            for (OpenAIMessageResponse message : messagesResponse.getData()) {
                if ("assistant".equals(message.getRole()) && message.getContent() != null && !message.getContent().isEmpty()) {
                    // Get the text content from the first content item
                    for (OpenAIMessageResponse.MessageContent content : message.getContent()) {
                        if ("text".equals(content.getType()) && content.getText() != null) {
                            return content.getText().getValue();
                        }
                    }
                }
            }

            return "Xin lỗi, tôi không thể tạo phản hồi lúc này. Vui lòng thử lại sau.";

        } catch (Exception e) {
            log.error("Error getting latest assistant response from thread {}", threadId, e);
            throw new RuntimeException("Failed to get assistant response", e);
        }
    }

    @Override
    public String processMessageWithAssistant(String threadId, String message, String assistantId) {
        try {
            //Thêm tin nhắn người dùng vào thread
            addMessageToThread(threadId, message);

            //gửi yêu cầu cho assistant
            OpenAIRunResponse run = runAssistant(threadId, assistantId);

            // Wait for completion with timeout
            String runId = run.getId();
            long startTime = System.currentTimeMillis();
            long timeoutMs = timeoutSeconds * 1000L;

            while (System.currentTimeMillis() - startTime < timeoutMs) {
                OpenAIRunResponse runStatus = getRunStatus(threadId, runId);
                String status = runStatus.getStatus();

                log.debug("Run {} status: {}", runId, status);

                if ("completed".equals(status)) {
                    // Lấy message trả về từ Assistant (qua thread)
                    return getLatestAssistantResponse(threadId);
                } else if ("failed".equals(status) || "cancelled".equals(status) || "expired".equals(status)) {
                    String errorMsg = runStatus.getLast_error() != null ?
                        runStatus.getLast_error().getMessage() : "Unknown error";
                    log.error("Run {} failed with status {} and error: {}", runId, status, errorMsg);
                    throw new RuntimeException("Assistant run failed: " + status);
                }

                // Wait before checking again
                Thread.sleep(1000);
            }

            // Timeout occurred
            log.error("Run {} timed out after {} seconds", runId, timeoutSeconds);
            throw new RuntimeException("Assistant response timed out");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted", e);
        } catch (Exception e) {
            log.error("Error processing message with assistant", e);
            throw new RuntimeException("Failed to process message with assistant", e);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");
        headers.set("OpenAI-Beta", BETA_HEADER);
        return headers;
    }
}
