package com.ttt.CosmeticStore.service.impl;

import com.cloudinary.Cloudinary;
import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.entity.Category;
import com.ttt.CosmeticStore.entity.Image;
import com.ttt.CosmeticStore.entity.Product;
import com.ttt.CosmeticStore.repository.CategoryRepository;
import com.ttt.CosmeticStore.repository.ImageRepository;
import com.ttt.CosmeticStore.repository.ProductRepository;
import com.ttt.CosmeticStore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final Cloudinary cloudinary;
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);

    }

    @Override
    public Optional<Product> getProductByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public Product createProduct(ProductRequest productRequest, MultipartFile imageFile) throws IOException {
        // Ánh xạ ProductRequest sang Product
        Product product = mapToProduct(productRequest);

        // Lưu sản phẩm
        product = productRepository.save(product);

        // Xử lý hình ảnh nếu có
        if (imageFile != null && !imageFile.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), Map.of());
            String url = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            Image image = new Image();
            image.setImageUrl(url);
            image.setPublicId(publicId);
            image.setType(imageFile.getContentType());
            image.setProduct(product);
            imageRepository.save(image);
        }

        return product;
    }

    @Override
    public ProductRequest mapToRequest(Product product) {
        ProductRequest dto = new ProductRequest();
        dto.setName(product.getName());
        dto.setType(product.getType());
        dto.setPrice(product.getPrice());
        dto.setInventory(product.getInventory());
        dto.setBenefits(product.getBenefits());
        dto.setDescription(product.getDescription());
        dto.setHowToUse(product.getHowToUse());
        dto.setIsBestSeller(product.getIsBestSeller());
        dto.setIsNew(product.getIsNew());
        dto.setCategoryId(product.getCategory().getId());
        return dto;
    }
    private Product mapToProduct(ProductRequest dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setType(dto.getType());
        product.setPrice(dto.getPrice());
        product.setInventory(dto.getInventory());
        product.setBenefits(dto.getBenefits());
        product.setDescription(dto.getDescription());
        product.setHowToUse(dto.getHowToUse());
        product.setIsBestSeller(dto.getIsBestSeller() != null ? dto.getIsBestSeller() : false);
        product.setIsNew(dto.getIsNew() != null ? dto.getIsNew() : false);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
        product.setCategory(category);

        return product;
    }
}
