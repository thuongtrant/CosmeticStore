package com.ttt.CosmeticStore.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIMessageResponse {
    private String id;
    private String object;
    private Long created_at;
    private String thread_id;
    private String role;
    private List<MessageContent> content;
    private String assistant_id;
    private String run_id;
    private List<String> file_ids;
    private Map<String, String> metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageContent {
        private String type;
        private TextContent text;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TextContent {
            private String value;
            private List<Object> annotations;
        }
    }
}
