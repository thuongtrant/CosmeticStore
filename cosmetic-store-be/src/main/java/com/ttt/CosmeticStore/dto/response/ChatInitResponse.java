
package com.ttt.CosmeticStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatInitResponse {
    private boolean success;
    private String message;
    private ChatRoomResponse chatRoom;
    private Long customerId;

    public ChatInitResponse(ChatRoomResponse chatRoom) {
        this.success = true;
        this.message = "Chat đã được khởi tạo thành công";
        this.chatRoom = chatRoom;
        this.customerId = chatRoom.getCustomerId();
    }

    public static ChatInitResponse error(String message) {
        ChatInitResponse response = new ChatInitResponse();
        response.success = false;
        response.message = message;
        return response;
    }
}