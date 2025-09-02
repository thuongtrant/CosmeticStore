package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.request.ProductSearchRequest;
import com.ttt.CosmeticStore.dto.response.PagedResponse;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import com.ttt.CosmeticStore.dto.response.ProductBasicInfo;
import com.ttt.CosmeticStore.dto.response.ProductByTypeResponse;
import com.ttt.CosmeticStore.dto.response.ProductListResponse;

public interface ProductService {
    PagedResponse<ProductListResponse> getProducts(int page, int size);
    PagedResponse<ProductBasicInfo> getProductsCus(int page, int size);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    PagedResponse<ProductBasicInfo> searchProducts(ProductSearchRequest searchRequest);
    PagedResponse<ProductByTypeResponse> getProductsByType(String type, int page, int size);
}
