package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.request.ProductSearchRequest;
import com.ttt.CosmeticStore.dto.response.PagedProductResponse;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    PagedProductResponse getAllProductsPaged(int page, int size);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    PagedProductResponse searchProducts(ProductSearchRequest searchRequest);
    PagedProductResponse getProductsByTypePaged(String type, int page, int size);
}
