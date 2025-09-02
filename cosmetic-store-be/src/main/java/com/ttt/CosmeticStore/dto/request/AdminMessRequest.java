package com.ttt.CosmeticStore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminMessRequest {
    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    @Size(max = 1000, message = "Tin nhắn không được vượt quá 1000 ký tự")
    private String content;

    @NotNull(message = "Chat room ID không được null")
    private Long chatRoomId;
}
