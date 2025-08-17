package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.request.ProductSearchRequest;
import com.ttt.CosmeticStore.dto.response.PagedProductResponse;
import com.ttt.CosmeticStore.dto.response.PagedSimpleProductResponse;
import com.ttt.CosmeticStore.dto.response.ProductDetailResponse;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import com.ttt.CosmeticStore.dto.response.ProductSimpleResponse;
import com.ttt.CosmeticStore.service.ProductService;
import com.ttt.CosmeticStore.service.CloudinaryService;
import com.ttt.CosmeticStore.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ApiProductController {

    private final ProductService productService;
    private final CloudinaryService cloudinaryService;
    private final ProductMapper productMapper;

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductResponse> getProductBasic(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/search")
    public ResponseEntity<PagedProductResponse> searchProducts(@RequestBody ProductSearchRequest searchRequest) {
        PagedProductResponse response = productService.searchProducts(searchRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedProductResponse> searchProductsGet(
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

        PagedProductResponse response = productService.searchProducts(searchRequest);
        return ResponseEntity.ok(response);
    }

    // Endpoint phân trang cho tất cả sản phẩm - 9 sản phẩm mỗi trang
    @GetMapping("/paged")
    public ResponseEntity<PagedSimpleProductResponse> getAllProductsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        PagedSimpleProductResponse response = productService.getAllProductsPaged(page, size);
        return ResponseEntity.ok(response);
    }

    // Endpoint phân trang cho sản phẩm theo loại - 4 sản phẩm mỗi trang
    @GetMapping("/by-type/paged")
    public ResponseEntity<PagedSimpleProductResponse> getProductsByTypePaged(
            @RequestParam(value = "productType", required = false, defaultValue = "new") String productType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        PagedSimpleProductResponse response = productService.getProductsByTypePaged(productType, page, size);
        return ResponseEntity.ok(response);
    }


//    @GetMapping("/by-type")
//    public ResponseEntity<List<ProductSimpleResponse>> getProductsByType(
//            @RequestParam(value = "productType", required = false, defaultValue = "new") String productType,
//            @RequestParam(defaultValue = "8") int limit) {
//        List<ProductSimpleResponse> products = productService.getProductsByType(productType, limit);
//        return ResponseEntity.ok(products);
//    }

//    @GetMapping("/list")
//    public ResponseEntity<List<ProductSimpleResponse>> getSimpleProducts() {
//        List<ProductResponse> allProducts = productService.getAllProducts();
//        List<ProductSimpleResponse> simpleProducts = allProducts.stream()
//                .map(productMapper::toSimpleResponse)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(simpleProducts);
//    }

//    @GetMapping("/{id}")
//    public ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable Long id) {
//        ProductDetailResponse productDetail = productService.getProductDetailById(id);
//        return ResponseEntity.ok(productDetail);
//    }
}
