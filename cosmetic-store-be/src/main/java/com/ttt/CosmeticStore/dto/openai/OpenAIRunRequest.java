package com.ttt.CosmeticStore.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIRunRequest {
    private String assistant_id;
    private String model;
    private String instructions;
    private String additional_instructions;
    private Map<String, String> metadata;

    public static OpenAIRunRequest withAssistant(String assistantId) {
        OpenAIRunRequest request = new OpenAIRunRequest();
        request.setAssistant_id(assistantId);
        return request;
    }
}
