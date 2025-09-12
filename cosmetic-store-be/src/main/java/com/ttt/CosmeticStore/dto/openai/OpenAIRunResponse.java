package com.ttt.CosmeticStore.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIRunResponse {
    private String id;
    private String object;
    private Long created_at;
    private String assistant_id;
    private String thread_id;
    private String status;
    private Long started_at;
    private Long expires_at;
    private Long cancelled_at;
    private Long failed_at;
    private Long completed_at;
    private LastError last_error;
    private String model;
    private String instructions;
    private List<Tool> tools;
    private List<String> file_ids;
    private Map<String, String> metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastError {
        private String code;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tool {
        private String type;
    }
}
