package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.request.ProductSearchRequest;
import com.ttt.CosmeticStore.dto.response.PagedResponse;
import com.ttt.CosmeticStore.dto.response.ProductBasicInfo;
import com.ttt.CosmeticStore.dto.response.ProductByTypeResponse;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import com.ttt.CosmeticStore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ApiProductController {

    private final ProductService productService;

    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductResponse> getProductDetail(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/search")
    public ResponseEntity<PagedResponse<ProductBasicInfo>> searchProducts(@RequestBody ProductSearchRequest searchRequest) {
        PagedResponse<ProductBasicInfo> response = productService.searchProducts(searchRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ProductBasicInfo>> searchProductsGet(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> ingredientIds,
            @RequestParam(required = false) List<Long> skinTypeIds,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) Boolean isBestSeller,
            @RequestParam(required = false) Boolean isNew,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        ProductSearchRequest searchRequest = new ProductSearchRequest();
        searchRequest.setKeyword(keyword);
        searchRequest.setCategoryIds(categoryIds);
        searchRequest.setIngredientIds(ingredientIds);
        searchRequest.setSkinTypeIds(skinTypeIds);
        searchRequest.setMinPrice(minPrice);
        searchRequest.setMaxPrice(maxPrice);
        searchRequest.setIsBestSeller(isBestSeller);
        searchRequest.setIsNew(isNew);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);
        searchRequest.setPage(page);
        searchRequest.setSize(size);

        PagedResponse<ProductBasicInfo> response = productService.searchProducts(searchRequest);
        return ResponseEntity.ok(response);
    }

    // Api phân trang cho tất cả sản phẩm phía khách hàng
    @GetMapping("/paged")
    public ResponseEntity<PagedResponse<ProductBasicInfo>> getAllProductsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
            PagedResponse<ProductBasicInfo> response = productService.getProductsCus(page, size);
            return ResponseEntity.ok(response);
    }

    // Api phân trang cho sản phẩm theo loại phía khách hàng
    @GetMapping("/by-type/paged")
    public ResponseEntity<PagedResponse<ProductByTypeResponse>> getProductsByTypePaged(
            @RequestParam(value = "productType", required = false, defaultValue = "new") String productType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
            PagedResponse<ProductByTypeResponse> response = productService.getProductsByType(productType, page, size);
            return ResponseEntity.ok(response);

    }
}
