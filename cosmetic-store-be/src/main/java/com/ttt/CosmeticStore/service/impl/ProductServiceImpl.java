package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.Util.PaginationUtil;
import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.request.ProductSearchRequest;
import com.ttt.CosmeticStore.dto.response.*;
import com.ttt.CosmeticStore.entity.*;
import com.ttt.CosmeticStore.exception.ResourceNotFoundException;
import com.ttt.CosmeticStore.mapper.PageProductMapper;
import com.ttt.CosmeticStore.mapper.ProductMapper;
import com.ttt.CosmeticStore.repository.*;
import com.ttt.CosmeticStore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final PageProductMapper pageProductMapper;

    @Override
    public PagedResponse<ProductListResponse> getProducts(int page, int size) {
        Pageable pageable = PaginationUtil.createPageablePS(page, size);
        Page<Product> productPage = productRepository.getProductsForAdmin(pageable);

        List<ProductListResponse> products = productPage.getContent().stream()
                .map(productMapper::toProductAResponse)
                .collect(Collectors.toList());

        return pageProductMapper.toPagedResponse(products, productPage);
    }

//    @Override
//    public List<ProductResponse> getAllProducts() {
//        return productRepository.findAll().stream()
//                .map(productMapper::toResponse)
//                .collect(Collectors.toList());
//    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id " + id));
        return productMapper.toResponse(product);
    }


    @Override
    public PagedResponse<ProductBasicInfo> searchProducts(ProductSearchRequest searchRequest) {
        // Tạo Pageable với sorting
        Pageable pageable = PaginationUtil.createPageable(
                searchRequest.getPage(),
                searchRequest.getSize(),
                searchRequest.getSortBy(),
                searchRequest.getSortDirection()
        );
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
        List<ProductBasicInfo> products = productPage.getContent().stream()
                .map(productMapper::toProductsResponse)
                .collect(Collectors.toList());

        // Tạo response
        return pageProductMapper.toPagedResponse(products, productPage);
    }


    @Override
    public PagedResponse<ProductBasicInfo> getProductsCus(int page, int size) {
        try {
            Pageable pageable = PaginationUtil.createPageablePS(page, size);
            Page<ProductBasicInfo> productPage = productRepository.getProductsForCus(pageable);

            List<ProductBasicInfo> products = productPage.getContent();


            return pageProductMapper.toPagedResponse(products, productPage);
        } catch (Exception e) {
            System.err.println("ERROR in getProductsCus: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public PagedResponse<ProductByTypeResponse> getProductsByType(String type, int page, int size) {
        try {
            Pageable pageable = PaginationUtil.createPageablePS(page, size);
            Page<Product> productPage;

            switch (type.toLowerCase()) {
                case "new":
                    productPage = productRepository.findByIsNewTrue(pageable);
                    break;
                case "bestseller":
                    productPage = productRepository.findByIsBestSellerTrue(pageable);
                    break;
                default:
                    throw new IllegalArgumentException("Khong tim thay loai san pham: " + type);
            }

            List<ProductByTypeResponse> products = productPage.getContent().stream()
                    .map(productMapper::toProductTypeResponse)
                    .collect(Collectors.toList());

            return pageProductMapper.toPagedResponse(products, productPage);
        } catch (Exception e) {
            System.err.println("ERROR in getProductsByType: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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

        productMapper.mapperImages(product, request.getImages());
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
        productMapper.updateImages(product, request.getImages());
        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id " + id));
        productRepository.delete(product);
    }
}
