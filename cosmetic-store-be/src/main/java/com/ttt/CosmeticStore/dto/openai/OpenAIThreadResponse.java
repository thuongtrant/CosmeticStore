package com.ttt.CosmeticStore.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIThreadResponse {
    private String id;
    private String object;
    private Long created_at;
    private Map<String, String> metadata;
}
