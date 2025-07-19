package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import com.ttt.CosmeticStore.entity.Category;
import com.ttt.CosmeticStore.entity.Image;
import com.ttt.CosmeticStore.entity.Product;
import com.ttt.CosmeticStore.exception.ResourceNotFoundException;
import com.ttt.CosmeticStore.mapper.ProductMapper;
import com.ttt.CosmeticStore.repository.CategoryRepository;
import com.ttt.CosmeticStore.repository.ProductRepository;
import com.ttt.CosmeticStore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

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
    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        // set category
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
            product.setCategory(category);
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
}
