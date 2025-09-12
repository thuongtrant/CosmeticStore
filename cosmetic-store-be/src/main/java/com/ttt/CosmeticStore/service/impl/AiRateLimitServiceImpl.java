package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.entity.AiRateLimit;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.repository.AiRateLimitRepository;
import com.ttt.CosmeticStore.repository.UserRepository;
import com.ttt.CosmeticStore.service.AiRateLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class AiRateLimitServiceImpl implements AiRateLimitService {

    @Autowired
    private AiRateLimitRepository aiRateLimitRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${ai.rate-limit.max-messages:3}")
    private int maxMessages;

    @Value("${ai.rate-limit.window-minutes:1}")
    private int windowMinutes;

    @Override
    public boolean checkAndUpdateRateLimit(Long userId) {
        try {
            Optional<AiRateLimit> rateLimitOpt = aiRateLimitRepository.findByUserId(userId);

            if (rateLimitOpt.isEmpty()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy user " + userId));

                AiRateLimit newRateLimit = new AiRateLimit(user);
                aiRateLimitRepository.save(newRateLimit);
                log.debug("Created new rate limit record for userId: {}", userId);
                return true;
            }

            AiRateLimit rateLimit = rateLimitOpt.get();

            // Check if rate limit exceeded
            if (rateLimit.isRateLimitExceeded(maxMessages, windowMinutes)) {
                log.warn("Rate limit exceeded for userId: {}. Count: {}, Window: {}",
                        userId, rateLimit.getMessageCount(), rateLimit.getWindowStart());
                return false;
            }

            // Update count
            rateLimit.incrementCount();
            aiRateLimitRepository.save(rateLimit);

            log.debug("Updated rate limit for userId: {}. Count: {}/{}",
                    userId, rateLimit.getMessageCount(), maxMessages);
            return true;

        } catch (Exception e) {
            log.error("Error checking rate limit for userId: {}", userId, e);
            return true;
        }
    }


    @Override
    public int getRemainingMessages(Long userId) {
        try {
            Optional<AiRateLimit> rateLimitOpt = aiRateLimitRepository.findByUserId(userId);

            if (rateLimitOpt.isEmpty()) {
                return maxMessages;
            }

            AiRateLimit rateLimit = rateLimitOpt.get();

            LocalDateTime windowEnd = rateLimit.getWindowStart().plusMinutes(windowMinutes);
            if (LocalDateTime.now().isAfter(windowEnd)) {
                return maxMessages;
            }

            return Math.max(0, maxMessages - rateLimit.getMessageCount());

        } catch (Exception e) {
            log.error("Error getting remaining messages for userId: {}", userId, e);
            return maxMessages; // Return full limit on error
        }
    }

    @Override
    public LocalDateTime getResetTime(Long userId) {
        try {
            Optional<AiRateLimit> rateLimitOpt = aiRateLimitRepository.findByUserId(userId);

            if (rateLimitOpt.isEmpty()) {
                return LocalDateTime.now();
            }

            return rateLimitOpt.get().getWindowStart().plusMinutes(windowMinutes);

        } catch (Exception e) {
            log.error("Error getting reset time for userId: {}", userId, e);
            return LocalDateTime.now();
        }
    }


}