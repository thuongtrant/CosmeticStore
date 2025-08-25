package com.ttt.CosmeticStore.repository;

import com.ttt.CosmeticStore.dto.response.ProductImageInfo;
import com.ttt.CosmeticStore.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("SELECT new com.ttt.CosmeticStore.dto.response.ProductImageInfo(i.product.id, i.imageUrl) " +
            "FROM Image i WHERE i.product.id IN :productIds ORDER BY i.product.id")
    List<ProductImageInfo> findImagesByProductIds(@Param("productIds") List<Long> productIds);
}