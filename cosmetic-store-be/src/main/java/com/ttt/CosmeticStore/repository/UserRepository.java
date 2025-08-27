package com.ttt.CosmeticStore.repository;

import com.ttt.CosmeticStore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Methods needed for OAuth2AuthenticationSuccessHandler
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    Long countNewUsers(@Param("startDate") LocalDateTime startDate,
                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT o.user.id) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    Long countActiveUsers(@Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate);
}
