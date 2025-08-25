package com.ttt.CosmeticStore.repository;

import com.ttt.CosmeticStore.entity.Cart;
import com.ttt.CosmeticStore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // Method chính - luôn fetch đầy đủ data
    @Query("SELECT c FROM Cart c " +
            "LEFT JOIN FETCH c.cartItems ci " +
            "LEFT JOIN FETCH ci.product p " +
            "LEFT JOIN FETCH p.category " +
            "WHERE c.user.id = :userId")
    Optional<Cart> findByUserId(@Param("userId") Long userId);

    // Chỉ dùng khi cần check exist mà không cần data
    boolean existsByUserId(Long userId);
}
