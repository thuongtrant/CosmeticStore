package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.request.ProductSearchRequest;
import com.ttt.CosmeticStore.dto.response.PagedProductResponse;
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

    @GetMapping("/list")
    public ResponseEntity<List<ProductSimpleResponse>> getSimpleProducts() {
        List<ProductResponse> allProducts = productService.getAllProducts();
        List<ProductSimpleResponse> simpleProducts = allProducts.stream()
                .map(productMapper::toSimpleResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(simpleProducts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable Long id) {
        ProductDetailResponse productDetail = productService.getProductDetailById(id);
        return ResponseEntity.ok(productDetail);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductResponse> getProductBasic(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ProductResponse> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") String price,
            @RequestParam("inventory") String inventory,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "benefits", required = false) String benefits,
            @RequestParam(value = "howToUse", required = false) String howToUse,
            @RequestParam(value = "isBestSeller", required = false, defaultValue = "false") Boolean isBestSeller,
            @RequestParam(value = "isNew", required = false, defaultValue = "false") Boolean isNew,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "mainImageFile", required = false) MultipartFile mainImageFile,
            @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
            @RequestParam(value = "ingredientIds", required = false) List<Long> ingredientIds,
            @RequestParam(value = "skinTypeIds", required = false) List<Long> skinTypeIds) {

        try {
            ProductRequest request = new ProductRequest();
            request.setName(name);
            request.setDescription(description);
            request.setPrice(java.math.BigDecimal.valueOf(Double.parseDouble(price)));
            request.setInventory(Integer.parseInt(inventory));
            request.setType(type);
            request.setBenefits(benefits);
            request.setHowToUse(howToUse);
            request.setIsBestSeller(isBestSeller);
            request.setIsNew(isNew);
            request.setCategoryId(categoryId);
            request.setIngredientIds(ingredientIds != null ? ingredientIds : new ArrayList<>());
            request.setSkinTypeIds(skinTypeIds != null ? skinTypeIds : new ArrayList<>());

            // Upload main image
            if (mainImageFile != null && !mainImageFile.isEmpty()) {
                String mainImageUrl = cloudinaryService.uploadImage(mainImageFile);
                request.setMainImage(mainImageUrl);
            }

            // Upload additional images
            List<String> imageUrls = new ArrayList<>();
            if (imageFiles != null) {
                for (MultipartFile imageFile : imageFiles) {
                    if (imageFile != null && !imageFile.isEmpty()) {
                        String imageUrl = cloudinaryService.uploadImage(imageFile);
                        imageUrls.add(imageUrl);
                    }
                }
            }
            request.setImages(imageUrls);

            ProductResponse response = productService.createProduct(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Giữ nguyên method JSON cho trường hợp không có file
    @PostMapping(value = "/json", consumes = {"application/json"})
    public ProductResponse createProductJson(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
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
}
