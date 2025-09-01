package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.dto.response.ChatRoomResponse;
import com.ttt.CosmeticStore.entity.ChatRoom;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatRoomMapper {

    /**
     * Chuyển đổi ChatRoom entity thành ChatRoomResponse DTO
     */
    public ChatRoomResponse toResponse(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .customerId(chatRoom.getCustomerId())
                .customerName(chatRoom.getCustomerName())
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageTime(chatRoom.getLastMessageTime())
                .unreadCount(chatRoom.getUnreadCount())
                .status(chatRoom.getStatus())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    /**
     * Chuyển đổi danh sách ChatRoom entities thành danh sách ChatRoomResponse DTOs
     */
    public List<ChatRoomResponse> toResponseList(List<ChatRoom> chatRooms) {
        return chatRooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }




}