package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.request.ProductSearchRequest;
import com.ttt.CosmeticStore.dto.response.PagedProductByTypeResponse;
import com.ttt.CosmeticStore.dto.response.PagedProductListResponse;
import com.ttt.CosmeticStore.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    PagedProductListResponse getProducts(int page, int size);
    PagedProductByTypeResponse getAllProductsPaged(int page, int size);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    PagedProductByTypeResponse searchProducts(ProductSearchRequest searchRequest);
    PagedProductByTypeResponse getProductsByType(String type, int page, int size);
}
