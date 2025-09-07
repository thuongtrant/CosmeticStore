package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.response.ChatRoomResponse;
import com.ttt.CosmeticStore.entity.ChatRoom;
import com.ttt.CosmeticStore.entity.ChatStatus;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.exception.ChatRoomNotFoundException;
import com.ttt.CosmeticStore.mapper.ChatRoomMapper;
import com.ttt.CosmeticStore.repository.ChatRoomRepository;
import com.ttt.CosmeticStore.repository.UserRepository;
import com.ttt.CosmeticStore.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomMapper chatMapper;

    @Override
    @Transactional
    public ChatRoomResponse createOrGetChatRoom(Long customerId, String customerName) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID không được null");
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name không được để trống");
        }

        // Load User entity từ database
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với ID: " + customerId));

        // Tìm tất cả active rooms
        List<ChatRoom> activeRooms = chatRoomRepository.findActiveByCustomerId(customerId);

        if (!activeRooms.isEmpty()) {
            ChatRoom chatRoom = handleExistingRooms(activeRooms, customer, customerName);
            return chatMapper.toResponse(chatRoom);
        }

        // Tạo room mới
        ChatRoom newRoom = createNewChatRoom(customer, customerName);
        return chatMapper.toResponse(newRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getAllActiveChatRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findByStatusOrderByLastMessageTimeDesc(ChatStatus.ACTIVE);
        return chatMapper.toResponseList(chatRooms);
    }

    @Override
    @Transactional
    public void markChatRoomAsRead(Long chatRoomId) {
        if (chatRoomId == null) {
            throw new IllegalArgumentException("Chat room ID không được null");
        }

        // Kiểm tra room tồn tại
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new ChatRoomNotFoundException("Chat room không tồn tại với ID: " + chatRoomId);
        }

        chatRoomRepository.markAsRead(chatRoomId);
    }

    @Override
    @Transactional
    public void updateLastMessage(Long chatRoomId, String message) {
        if (chatRoomId == null) {
            throw new IllegalArgumentException("Chat room ID không được null");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message không được để trống");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatRoomNotFoundException("Chat room không tồn tại với ID: " + chatRoomId));

        chatRoom.setLastMessage(message.trim());
        chatRoom.setLastMessageTime(LocalDateTime.now());
        chatRoom.setUnreadCount(chatRoom.getUnreadCount() + 1);
        chatRoomRepository.save(chatRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRoomResponse getChatRoomById(Long chatRoomId) {
        if (chatRoomId == null) {
            throw new IllegalArgumentException("Chat room ID không được null");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatRoomNotFoundException("Chat room không tồn tại với ID: " + chatRoomId));
        return chatMapper.toResponse(chatRoom);
    }


    private ChatRoom handleExistingRooms(List<ChatRoom> activeRooms, User customer, String customerName) {
        ChatRoom chatRoom;

        if (activeRooms.size() > 1) {
            // Sắp xếp theo thời gian tạo, giữ lại cái mới nhất
            activeRooms.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            chatRoom = activeRooms.get(0);

            // Archive các room cũ
            for (int i = 1; i < activeRooms.size(); i++) {
                ChatRoom oldRoom = activeRooms.get(i);
                oldRoom.setStatus(ChatStatus.ARCHIVED);
                chatRoomRepository.save(oldRoom);
            }
        } else {
            chatRoom = activeRooms.get(0);
        }

        // Update customer name nếu cần và đồng bộ từ User entity
        String actualCustomerName = customer.getUsername(); // Hoặc field tên khác từ User
        if (!actualCustomerName.equals(chatRoom.getCustomerName())) {
            chatRoom.setCustomerName(actualCustomerName);
            chatRoom = chatRoomRepository.save(chatRoom);
        }

        return chatRoom;
    }

    private ChatRoom createNewChatRoom(User customer, String customerName) {
        ChatRoom newRoom = new ChatRoom();
        newRoom.setCustomer(customer); // Set User entity thay vì customerId
        newRoom.setCustomerName(customer.getUsername()); // Lấy tên từ User entity
        newRoom.setStatus(ChatStatus.ACTIVE);
        newRoom.setCreatedAt(LocalDateTime.now());
        newRoom.setUnreadCount(0);

        return chatRoomRepository.save(newRoom);
    }
}