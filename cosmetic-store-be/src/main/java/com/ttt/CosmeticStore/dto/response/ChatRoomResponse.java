package com.ttt.CosmeticStore.dto.response;

import com.ttt.CosmeticStore.entity.ChatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
    private ChatStatus status;
    private LocalDateTime createdAt;
}
