package com.ttt.CosmeticStore.repository;

import com.ttt.CosmeticStore.entity.AiRateLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiRateLimitRepository extends JpaRepository<AiRateLimit, Long> {

    // Tìm rate limit record theo userId
    Optional<AiRateLimit> findByUserId(Long userId);

}
