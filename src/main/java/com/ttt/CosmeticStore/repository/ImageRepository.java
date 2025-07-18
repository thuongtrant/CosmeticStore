package com.ttt.CosmeticStore.repository;

import com.ttt.CosmeticStore.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByProductId(Long productId);

}
