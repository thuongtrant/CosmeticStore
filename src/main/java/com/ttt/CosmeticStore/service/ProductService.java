package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.request.ProductSearchRequest;
import com.ttt.CosmeticStore.dto.response.PagedProductResponse;
import com.ttt.CosmeticStore.dto.response.ProductDetailResponse;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductDetailResponse getProductDetailById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    PagedProductResponse searchProducts(ProductSearchRequest searchRequest);
}
