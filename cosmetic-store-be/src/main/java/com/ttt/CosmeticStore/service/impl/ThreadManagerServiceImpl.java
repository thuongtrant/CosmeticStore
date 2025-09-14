package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.openai.OpenAIThreadResponse;
import com.ttt.CosmeticStore.service.OpenAIAssistantApiService;
import com.ttt.CosmeticStore.service.ThreadManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class ThreadManagerServiceImpl implements ThreadManagerService {

    @Autowired
    private OpenAIAssistantApiService openAIAssistantApiService;

    private final ConcurrentMap<Long, String> userThreads = new ConcurrentHashMap<>();

    @Override
    public String getOrCreateThreadForUser(Long userId) {
        return userThreads.computeIfAbsent(userId, this::createThreadForUser);
    }

    @Override
    public String getThreadForUser(Long userId) {
        return userThreads.get(userId);
    }

    @Override
    public String createNewThreadForUser(Long userId) {
        String threadId = createThreadForUser(userId);
        userThreads.put(userId, threadId);
        return threadId;
    }

    @Override
    public void clearThreadForUser(Long userId) {
        String removedThreadId = userThreads.remove(userId);
        if (removedThreadId != null) {
            log.info("Cleared thread {} for user {}", removedThreadId, userId);
        }
    }

    private String createThreadForUser(Long userId) {
        try {
            OpenAIThreadResponse thread = openAIAssistantApiService.createThread(userId);
            log.info("Created new thread {} for user {}", thread.getId(), userId);
            return thread.getId();
        } catch (Exception e) {
            log.error("Failed to create thread for user {}", userId, e);
            throw new RuntimeException("Could not create conversation thread", e);
        }
    }
}