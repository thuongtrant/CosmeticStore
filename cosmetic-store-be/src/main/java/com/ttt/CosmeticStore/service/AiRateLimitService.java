package com.ttt.CosmeticStore.service;

public interface AiRateLimitService {

    // Kiểm tra và cập nhật rate limit
    boolean checkAndUpdateRateLimit(Long userId);

    // Lấy số tin nhắn còn lại
    int getRemainingMessages(Long userId);

    // Lấy thời gian reset rate limit
    java.time.LocalDateTime getResetTime(Long userId);

}