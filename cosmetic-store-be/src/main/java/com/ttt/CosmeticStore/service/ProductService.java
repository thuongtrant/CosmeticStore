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
    List<ProductSimpleResponse> getProductsByType(String type, int limit);
    PagedProductResponse searchProducts(ProductSearchRequest searchRequest);
    PagedSimpleProductResponse getProductsByTypePaged(String type, int page, int size);
}
