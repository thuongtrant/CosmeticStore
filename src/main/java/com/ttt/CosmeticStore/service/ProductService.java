package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    Product saveProduct(Product product);

    void deleteProduct(Long id);

    Optional<Product> getProductByName(String name);

    List<Product> getProductsByCategoryId(Long categoryId);

    List<Product> searchProducts(String keyword);
    Product createProduct(ProductRequest productRequest, MultipartFile imageFile) throws IOException;
    ProductRequest mapToRequest(Product product);
}
