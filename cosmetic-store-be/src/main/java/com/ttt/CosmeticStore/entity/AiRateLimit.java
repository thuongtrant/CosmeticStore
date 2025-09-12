package com.ttt.CosmeticStore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_rate_limit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiRateLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_ai_rate_limit_user"))
    private User user;

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer messageCount;

    @Column(nullable = false)
    private LocalDateTime windowStart;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public AiRateLimit(User user) {
        this.user = user;
        this.messageCount = 1;
        this.windowStart = LocalDateTime.now();
    }

    // Convenience constructor với userId (for backward compatibility)
    public AiRateLimit(Long userId) {
        this.userId = userId;
        this.messageCount = 1;
        this.windowStart = LocalDateTime.now();
    }

    public void resetWindow() {
        this.messageCount = 1;
        this.windowStart = LocalDateTime.now();
    }

    public void incrementCount() {
        this.messageCount++;
    }

    public boolean isRateLimitExceeded(int maxMessages, int windowMinutes) {
        LocalDateTime windowEnd = this.windowStart.plusMinutes(windowMinutes);
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(windowEnd)) {
            resetWindow();
            return false;
        }

        return this.messageCount > maxMessages;
    }
    public Long getUserId() {
        return user != null ? user.getId() : userId;
    }
}
