package com.ttt.CosmeticStore.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIMessageRequest {
    private String role;
    private String content;
    private Map<String, String> metadata;

    public static OpenAIMessageRequest userMessage(String content) {
        OpenAIMessageRequest request = new OpenAIMessageRequest();
        request.setRole("user");
        request.setContent(content);
        return request;
    }
}
