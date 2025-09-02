package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.response.ChatRoomResponse;

import java.util.List;

public interface ChatService {
    ChatRoomResponse createOrGetChatRoom(Long customerId, String customerName);
    List<ChatRoomResponse> getAllActiveChatRooms();
    void markChatRoomAsRead(Long chatRoomId);
    void updateLastMessage(Long chatRoomId, String message);
    ChatRoomResponse getChatRoomById(Long chatRoomId);
}
