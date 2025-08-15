package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.request.ProductSearchRequest;
import com.ttt.CosmeticStore.dto.response.PagedProductResponse;
import com.ttt.CosmeticStore.dto.response.PagedSimpleProductResponse;
import com.ttt.CosmeticStore.dto.response.ProductDetailResponse;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import com.ttt.CosmeticStore.dto.response.ProductSimpleResponse;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    PagedSimpleProductResponse getAllProductsPaged(int page, int size);
    ProductResponse getProductById(Long id);
    ProductDetailResponse getProductDetailById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    PagedProductResponse searchProducts(ProductSearchRequest searchRequest);
    List<ProductSimpleResponse> getProductsByType(String type, int limit);
    PagedSimpleProductResponse getProductsByTypePaged(String type, int page, int size);
}
