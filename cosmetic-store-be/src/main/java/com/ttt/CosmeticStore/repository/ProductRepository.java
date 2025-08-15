package com.ttt.CosmeticStore.repository;

import com.ttt.CosmeticStore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.images " +
           "LEFT JOIN FETCH p.ingredients " +
           "LEFT JOIN FETCH p.skinTypes " +
           "WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN p.category c " +
           "LEFT JOIN p.ingredients i " +
           "LEFT JOIN p.skinTypes s " +
           "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryIds IS NULL OR c.id IN :categoryIds) " +
           "AND (:ingredientIds IS NULL OR i.id IN :ingredientIds) " +
           "AND (:skinTypeIds IS NULL OR s.id IN :skinTypeIds) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:isBestSeller IS NULL OR p.isBestSeller = :isBestSeller) " +
           "AND (:isNew IS NULL OR p.isNew = :isNew)")
    Page<Product> findProductsWithFilters(
            @Param("keyword") String keyword,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("ingredientIds") List<Long> ingredientIds,
            @Param("skinTypeIds") List<Long> skinTypeIds,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("isBestSeller") Boolean isBestSeller,
            @Param("isNew") Boolean isNew,
            Pageable pageable);

    // Lấy sản phẩm mới nhất
    List<Product> findTop8ByIsNewTrueOrderByIdDesc();

    // Lấy sản phẩm bán chạy
    List<Product> findTop8ByIsBestSellerTrueOrderByIdDesc();

    // Phân trang cho sản phẩm mới nhất
    Page<Product> findByIsNewTrue(Pageable pageable);

    // Phân trang cho sản phẩm bán chạy
    Page<Product> findByIsBestSellerTrue(Pageable pageable);
}
