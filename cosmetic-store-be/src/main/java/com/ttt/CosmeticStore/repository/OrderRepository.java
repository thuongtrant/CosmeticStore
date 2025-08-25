package com.ttt.CosmeticStore.repository;

import com.ttt.CosmeticStore.entity.Order;
import com.ttt.CosmeticStore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.product p " +
            "LEFT JOIN FETCH o.payment " +
            "LEFT JOIN FETCH o.shippingAddress " +
            "WHERE o.user = :user " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByUserWithDetailsOrderByCreatedAtDesc(@Param("user") User user);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.product p " +
            "LEFT JOIN FETCH o.payment " +
            "LEFT JOIN FETCH o.shippingAddress " +
            "WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberWithDetails(@Param("orderNumber") String orderNumber);

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    // Tìm kiếm theo số đơn hàng
    Page<Order> findByOrderNumberContainingIgnoreCase(String orderNumber, Pageable pageable);

    // Đếm đơn hàng theo khoảng thời gian
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Tìm đơn hàng theo khoảng thời gian
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    // Tìm đơn hàng theo trạng thái và khoảng thời gian
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByStatusAndCreatedAtBetween(@Param("status") Order.OrderStatus status,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    // Thống kê doanh thu theo ngày
    @Query("SELECT DATE(o.createdAt), SUM(o.totalAmount), COUNT(o), COUNT(DISTINCT o.user) " +
           "FROM Order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status = :status " +
           "GROUP BY DATE(o.createdAt) " +
           "ORDER BY DATE(o.createdAt)")
    List<Object[]> findRevenueStatistics(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        @Param("status") Order.OrderStatus status);

    // Tổng doanh thu
    @Query("SELECT SUM(o.totalAmount) FROM Order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status = :status")
    BigDecimal getTotalRevenue(@Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate,
                              @Param("status") Order.OrderStatus status);

    // Đếm số đơn hàng theo trạng thái và thời gian
    Long countByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, Order.OrderStatus status);

    // Đếm số khách hàng khác nhau
    @Query("SELECT COUNT(DISTINCT o.user) FROM Order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate AND o.status = :status")
    Long countDistinctCustomers(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate,
                               @Param("status") Order.OrderStatus status);
}
