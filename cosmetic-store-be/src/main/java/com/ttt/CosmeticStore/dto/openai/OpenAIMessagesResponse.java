package com.ttt.CosmeticStore.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIMessagesResponse {
    private String object;
    private List<OpenAIMessageResponse> data;
    private String first_id;
    private String last_id;
    private boolean has_more;
}
