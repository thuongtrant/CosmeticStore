package com.ttt.CosmeticStore.repository;

import com.ttt.CosmeticStore.entity.Order;
import com.ttt.CosmeticStore.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Thống kê sản phẩm bán chạy - sử dụng native query
    @Query(value = "SELECT p.id, p.name, c.name as category_name, " +
           "SUM(oi.quantity) as total_sold, SUM(oi.unit_price * oi.quantity) as total_revenue, p.inventory, " +
           "(SELECT i.image_url FROM image i WHERE i.product_id = p.id LIMIT 1) as image_url " +
           "FROM order_items oi " +
           "JOIN orders o ON oi.order_id = o.id " +
           "JOIN product p ON oi.product_id = p.id " +
           "LEFT JOIN category c ON p.category_id = c.id " +
           "WHERE o.created_at BETWEEN :startDate AND :endDate AND o.status = :status " +
           "GROUP BY p.id, p.name, c.name, p.inventory " +
           "ORDER BY SUM(oi.quantity) DESC",
           nativeQuery = true)
    List<Object[]> findTopSellingProducts(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         @Param("status") String status,
                                         Pageable pageable);
}
