package com.ttt.CosmeticStore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // Giữ lại customer_name để cache tên, nhưng sẽ đồng bộ từ User
    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "last_message", columnDefinition = "TEXT")
    private String lastMessage;

    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;

    @Column(name = "unread_count", columnDefinition = "INT DEFAULT 0")
    private Integer unreadCount = 0;

    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private ChatStatus status = ChatStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Helper methods để tương thích với code hiện tại
    public Long getCustomerId() {
        return customer != null ? customer.getId() : null;
    }

    public void setCustomerId(Long customerId) {
        // Method này sẽ được deprecated, khuyến khích sử dụng setCustomer()
        if (customerId != null && (customer == null || !customerId.equals(customer.getId()))) {
            // Cần load User từ database khi set customerId
            User user = new User();
            user.setId(customerId);
            this.customer = user;
        }
    }
}