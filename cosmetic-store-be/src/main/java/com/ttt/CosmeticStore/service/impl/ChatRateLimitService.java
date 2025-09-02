package com.ttt.CosmeticStore.service.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatRateLimitService   {
    private final Map<String, List<Long>> userRequestTimes = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 30;
    private static final long MINUTE_IN_MILLIS = 60 * 1000;

    public boolean isAllowed(String userId) {
        long now = System.currentTimeMillis();

        userRequestTimes.computeIfAbsent(userId, k -> new ArrayList<>());
        List<Long> requestTimes = userRequestTimes.get(userId);

        // Remove old requests
        requestTimes.removeIf(time -> now - time > MINUTE_IN_MILLIS);

        if (requestTimes.size() >= MAX_REQUESTS_PER_MINUTE) {
            return false;
        }

        requestTimes.add(now);
        return true;
    }

    @Scheduled(fixedRate = 300000) // Clean up every 5 minutes
    public void cleanup() {
        long now = System.currentTimeMillis();
        userRequestTimes.entrySet().removeIf(entry ->
                entry.getValue().isEmpty() ||
                        now - entry.getValue().get(entry.getValue().size() - 1) > MINUTE_IN_MILLIS
        );
    }
}
