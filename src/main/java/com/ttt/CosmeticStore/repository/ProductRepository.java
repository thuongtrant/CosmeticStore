package com.ttt.CosmeticStore.repository;

import com.ttt.CosmeticStore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}
