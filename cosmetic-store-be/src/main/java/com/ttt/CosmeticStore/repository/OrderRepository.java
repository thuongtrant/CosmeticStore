package com.ttt.CosmeticStore.repository;

import com.ttt.CosmeticStore.dto.response.OrdersResponseA;
import com.ttt.CosmeticStore.dto.response.OrdersResponseC;
import com.ttt.CosmeticStore.entity.Order;
import com.ttt.CosmeticStore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT new com.ttt.CosmeticStore.dto.response.OrdersResponseC(" +
            "o.id, o.orderNumber, o.totalAmount, o.status, " +
            "o.createdAt) " +
            "FROM Order o " +
            "ORDER BY o.createdAt DESC")
    Page<OrdersResponseC> myOrders(@Param("user") User user, Pageable page);


    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.product p " +
            "LEFT JOIN FETCH o.payment " +
            "LEFT JOIN FETCH o.shippingAddress " +
            "WHERE o.orderNumber = :orderNumber")
    Optional<Order> getOrderDetail(@Param("orderNumber") String orderNumber);

    @Query("SELECT new com.ttt.CosmeticStore.dto.response.OrdersResponseA(" +
            "o.id, o.orderNumber, o.totalAmount, o.status, " +
            "CONCAT(sa.addressLine, ', ', sa.ward, ', ', sa.district, ', ', sa.province), " +
            "o.createdAt) " +
            "FROM Order o " +
            "JOIN o.shippingAddress sa " +
            "ORDER BY o.createdAt DESC")
    Page<OrdersResponseA> getOrdersForAd(Pageable pageable);

    @Query("SELECT new com.ttt.CosmeticStore.dto.response.OrdersResponseA(" +
            "o.id, o.orderNumber, o.totalAmount, o.status, " +
            "CONCAT(sa.addressLine, ', ', sa.ward, ', ', sa.district, ', ', sa.province), " +
            "o.createdAt) " +
            "FROM Order o " +
            "JOIN o.shippingAddress sa " +
            "WHERE o.status = :status " +
            "ORDER BY o.createdAt DESC")
    Page<OrdersResponseA> findByStatus(Order.OrderStatus status, Pageable pageable);

    @Query("SELECT new com.ttt.CosmeticStore.dto.response.OrdersResponseA(" +
            "o.id, o.orderNumber, o.totalAmount, o.status, " +
            "CONCAT(sa.addressLine, ', ', sa.ward, ', ', sa.district, ', ', sa.province), " +
            "o.createdAt) " +
            "FROM Order o " +
            "JOIN o.shippingAddress sa " +
            "WHERE o.orderNumber LIKE %:orderNumber% " +
            "ORDER BY o.createdAt DESC")
    Page<OrdersResponseA> findByOrderNumber(@Param("orderNumber") String orderNumber, Pageable pageable);


    @Modifying
    @Query("UPDATE Order o SET o.status = :status, o.updatedAt = :updatedAt WHERE o.id = :id")
    void updateStatus(@Param("id") Long id,
                      @Param("status") Order.OrderStatus status,
                      @Param("updatedAt") LocalDateTime updatedAt);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "AND o.status NOT IN ('CANCELLED')")
    BigDecimal getTotalRevenue(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate")
    Long countOrdersByDateRange(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o.status, COUNT(o) FROM Order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY o.status")
    List<Object[]> countOrdersByStatus(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p.id, p.name, SUM(oi.quantity), SUM(oi.totalPrice), c.name " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN oi.product p " +
            "LEFT JOIN p.category c " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "AND o.status IN ('DELIVERED') " +
            "GROUP BY p.id, p.name, c.name " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getTopSellingProducts(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         @Param("limit") int limit);

    @Query("SELECT COALESCE(c.id, 0), COALESCE(c.name, 'Chưa phân loại'), " +
            "SUM(oi.quantity), SUM(oi.totalPrice), COUNT(DISTINCT o.id) " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "JOIN oi.product p " +
            "LEFT JOIN p.category c " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "AND o.status NOT IN ('CANCELLED') " +
            "GROUP BY c.id, c.name " +
            "ORDER BY SUM(oi.totalPrice) DESC")
    List<Object[]> getCategoryStats(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(o.createdAt), COALESCE(SUM(o.totalAmount), 0), COUNT(o) " +
            "FROM Order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "AND o.status NOT IN ('CANCELLED') " +
            "GROUP BY DATE(o.createdAt) " +
            "ORDER BY DATE(o.createdAt)")
    List<Object[]> getDailyRevenue(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT YEAR(o.createdAt), MONTH(o.createdAt), " +
            "COALESCE(SUM(o.totalAmount), 0), COUNT(o) " +
            "FROM Order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "AND o.status NOT IN ('CANCELLED') " +
            "GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) " +
            "ORDER BY YEAR(o.createdAt), MONTH(o.createdAt)")
    List<Object[]> getMonthlyRevenue(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

}
