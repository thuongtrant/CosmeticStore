package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.request.ProductSearchRequest;
import com.ttt.CosmeticStore.dto.response.PagedProductResponse;
import com.ttt.CosmeticStore.dto.response.ProductDetailResponse;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import com.ttt.CosmeticStore.dto.response.ProductSimpleResponse;
import com.ttt.CosmeticStore.entity.*;
import com.ttt.CosmeticStore.exception.ResourceNotFoundException;
import com.ttt.CosmeticStore.mapper.ProductMapper;
import com.ttt.CosmeticStore.repository.CategoryRepository;
import com.ttt.CosmeticStore.repository.IngredientRepository;
import com.ttt.CosmeticStore.repository.ProductRepository;
import com.ttt.CosmeticStore.repository.SkinTypeRepository;
import com.ttt.CosmeticStore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final IngredientRepository ingredientRepository;
    private final SkinTypeRepository skinTypeRepository;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id " + id));
        return productMapper.toResponse(product);
    }

    @Override
    public ProductDetailResponse getProductDetailById(Long id) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id " + id));
        return productMapper.toDetailResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        // set category
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
            product.setCategory(category);
        }
        // Lấy list Ingredient theo id
        if (request.getIngredientIds() != null && !request.getIngredientIds().isEmpty()) {
            List<Ingredient> ingredients = ingredientRepository.findAllById(request.getIngredientIds());
            product.setIngredients(ingredients);
        } else {
            product.setIngredients(new ArrayList<>());
        }

        // Lấy list SkinType theo id
        if (request.getSkinTypeIds() != null && !request.getSkinTypeIds().isEmpty()) {
            List<SkinType> skinTypes = skinTypeRepository.findAllById(request.getSkinTypeIds());
            product.setSkinTypes(skinTypes);
        } else {
            product.setSkinTypes(new ArrayList<>());
        }
        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id " + id));

        // update thông tin cơ bản
        productMapper.updateEntity(product, request);

        // update category
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
            product.setCategory(category);
        }
        if (request.getIngredientIds() != null) {
            List<Ingredient> ingredients = ingredientRepository.findAllById(request.getIngredientIds());
            product.setIngredients(ingredients);
        }

        // Lấy list SkinType theo id mới (nếu truyền lên)
        if (request.getSkinTypeIds() != null) {
            List<SkinType> skinTypes = skinTypeRepository.findAllById(request.getSkinTypeIds());
            product.setSkinTypes(skinTypes);
        }
        // update images
        if (request.getImages() != null) {
            product.getImages().clear(); // xóa hết ảnh cũ
            List<Image> imageEntities = request.getImages().stream()
                    .map(url -> {
                        Image img = new Image();
                        img.setImageUrl(url);
                        img.setProduct(product);
                        return img;
                    }).collect(Collectors.toList());
            product.getImages().addAll(imageEntities);
        }
        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id " + id));
        productRepository.delete(product);
    }

    @Override
    public PagedProductResponse searchProducts(ProductSearchRequest searchRequest) {
        // Tạo Pageable với sorting
        Pageable pageable = createPageable(searchRequest);

        // Xử lý filter parameters
        String keyword = searchRequest.getKeyword();
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        List<Long> categoryIds = searchRequest.getCategoryIds();
        if (categoryIds != null && categoryIds.isEmpty()) {
            categoryIds = null;
        }

        List<Long> ingredientIds = searchRequest.getIngredientIds();
        if (ingredientIds != null && ingredientIds.isEmpty()) {
            ingredientIds = null;
        }

        List<Long> skinTypeIds = searchRequest.getSkinTypeIds();
        if (skinTypeIds != null && skinTypeIds.isEmpty()) {
            skinTypeIds = null;
        }

        // Gọi repository để tìm kiếm
        Page<Product> productPage = productRepository.findProductsWithFilters(
                keyword,
                categoryIds,
                ingredientIds,
                skinTypeIds,
                searchRequest.getMinPrice(),
                searchRequest.getMaxPrice(),
                searchRequest.getIsBestSeller(),
                searchRequest.getIsNew(),
                pageable
        );

        // Convert sang DTO
        List<ProductSimpleResponse> products = productPage.getContent().stream()
                .map(productMapper::convertToSimpleResponse)
                .collect(Collectors.toList());

        // Tạo response
        PagedProductResponse response = new PagedProductResponse();
        response.setProducts(products);
        response.setCurrentPage(productPage.getNumber());
        response.setTotalPages(productPage.getTotalPages());
        response.setTotalElements(productPage.getTotalElements());
        response.setSize(productPage.getSize());
        response.setHasNext(productPage.hasNext());
        response.setHasPrevious(productPage.hasPrevious());

        return response;
    }

    private Pageable createPageable(ProductSearchRequest searchRequest) {
        String sortBy = searchRequest.getSortBy();
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "id"; // default sort
        }

        String sortDirection = searchRequest.getSortDirection();
        Sort.Direction direction = Sort.Direction.ASC;
        if ("DESC".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.DESC;
        }

        // Validate sortBy field
        switch (sortBy.toLowerCase()) {
            case "price":
            case "name":
            case "createdat":
            case "id":
                break;
            default:
                sortBy = "id"; // fallback to safe default
        }

        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
    }
}
