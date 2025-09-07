package com.ttt.CosmeticStore.repository;

import com.ttt.CosmeticStore.entity.ChatRoom;
import com.ttt.CosmeticStore.entity.ChatStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT c FROM ChatRoom c WHERE c.customer.id = :customerId AND c.status = 'ACTIVE'")
    List<ChatRoom> findActiveByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT c FROM ChatRoom c WHERE c.customer.id = :customerId ORDER BY c.createdAt DESC")
    List<ChatRoom> findAllByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT c FROM ChatRoom c WHERE c.status = :status ORDER BY c.lastMessageTime DESC")
    List<ChatRoom> findByStatusOrderByLastMessageTimeDesc(@Param("status") ChatStatus status);

    @Query("UPDATE ChatRoom c SET c.unreadCount = 0 WHERE c.id = :chatRoomId")
    @Modifying
    @Transactional
    void markAsRead(@Param("chatRoomId") Long chatRoomId);
}
