package com.ttt.CosmeticStore.service;

public interface ThreadManagerService {

    /**
     * Get or create a thread ID for a user
     */
    String getOrCreateThreadForUser(Long userId);

    /**
     * Get existing thread ID for a user
     */
    String getThreadForUser(Long userId);

    /**
     * Create a new thread for a user (replaces existing)
     */
    String createNewThreadForUser(Long userId);

    /**
     * Clear thread for user (for new conversations)
     */
    void clearThreadForUser(Long userId);
}
