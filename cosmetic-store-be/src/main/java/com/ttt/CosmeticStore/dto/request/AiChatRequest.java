package com.ttt.CosmeticStore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {
    private Long userId;

    @NotBlank(message = "Tin nhắn không được để trống")
    @Size(min = 1, max = 500, message = "Tin nhắn phải từ 1 đến 500 ký tự")
    private String message;

    private String skinType;
    private String concern;
    private String chatRoomId;
}
