package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.response.ProductBasicInfo;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import com.ttt.CosmeticStore.dto.response.ProductByTypeResponse;
import com.ttt.CosmeticStore.dto.response.ProductListResponse;
import com.ttt.CosmeticStore.entity.Image;
import com.ttt.CosmeticStore.entity.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setBenefits(product.getBenefits());
        response.setHowToUse(product.getHowToUse());
        response.setType(product.getType());
        response.setPrice(product.getPrice());
        response.setInventory(product.getInventory());
        response.setIsBestSeller(product.getIsBestSeller());
        response.setIsNew(product.getIsNew());
        response.setMainImage(product.getMainImageUrl());

        // Set category info
        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }

        // Set skin types with IDs
        if (product.getSkinTypes() != null) {
            response.setSkinTypeIds(product.getSkinTypes().stream()
                    .map(skinType -> skinType.getId())
                    .collect(Collectors.toList()));
            response.setSkinTypes(product.getSkinTypes().stream()
                    .map(skinType -> skinType.getName())
                    .collect(Collectors.toList()));
        }

        // Set ingredients with IDs
        if (product.getIngredients() != null) {
            response.setIngredientIds(product.getIngredients().stream()
                    .map(ingredient -> ingredient.getId())
                    .collect(Collectors.toList()));
            response.setIngredients(product.getIngredients().stream()
                    .map(ingredient -> ingredient.getName())
                    .collect(Collectors.toList()));
        }

        // Set image URLs
        if (product.getImages() != null) {
            response.setImages(product.getImages().stream()
                    .map(image -> image.getImageUrl())
                    .collect(Collectors.toList()));
        }

        return response;
    }

    public ProductByTypeResponse toSimpleResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductByTypeResponse simple = new ProductByTypeResponse();
        simple.setId(product.getId());
        simple.setName(product.getName());
        simple.setPrice(product.getPrice());
        simple.setIsBestSeller(product.getIsBestSeller());
        simple.setIsNew(product.getIsNew());
        simple.setMainImageUrl(product.getMainImageUrl());

        return simple;
    }

    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBenefits(request.getBenefits());
        product.setHowToUse(request.getHowToUse());
        product.setType(request.getType());
        product.setPrice(request.getPrice());
        product.setInventory(request.getInventory());
        product.setIsBestSeller(request.getIsBestSeller() != null ? request.getIsBestSeller() : false);
        product.setIsNew(request.getIsNew() != null ? request.getIsNew() : false);
        product.setMainImageUrl(request.getMainImage());

        return product;
    }

    public void updateEntity(Product product, ProductRequest request) {
        if (product == null || request == null) {
            return;
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBenefits(request.getBenefits());
        product.setHowToUse(request.getHowToUse());
        product.setType(request.getType());
        product.setPrice(request.getPrice());
        product.setInventory(request.getInventory());
        product.setIsBestSeller(request.getIsBestSeller() != null ? request.getIsBestSeller() : false);
        product.setIsNew(request.getIsNew() != null ? request.getIsNew() : false);
        if (request.getMainImage() != null) {
            product.setMainImageUrl(request.getMainImage());
        }
    }

    public ProductListResponse toListResponse(ProductBasicInfo basic) {
        if (basic == null) {
            return null;
        }

        ProductListResponse response = new ProductListResponse();
        response.setId(basic.getId());
        response.setName(basic.getName());
        response.setPrice(basic.getPrice());
        response.setMainImage(basic.getMainImageUrl());
        response.setCategoryName(basic.getCategoryName());
        return response;
    }

    // Phương thức mới để xử lý ánh xạ ảnh (cho create)
    public void mapperImages(Product product, List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            List<Image> imageEntities = imageUrls.stream()
                    .map(url -> {
                        Image image = new Image();
                        image.setImageUrl(url);
                        image.setProduct(product);
                        return image;
                    })
                    .collect(Collectors.toList());
            product.setImages(imageEntities);
        } else {
            product.setImages(new ArrayList<>());
        }
    }

    // Phương thức mới để xử lý ánh xạ ảnh (cho update)
    public void updateImages(Product product, List<String> imageUrls) {
        product.getImages().clear();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            List<Image> imageEntities = imageUrls.stream()
                    .map(url -> {
                        Image image = new Image();
                        image.setImageUrl(url);
                        image.setProduct(product);
                        return image;
                    })
                    .collect(Collectors.toList());
            product.getImages().addAll(imageEntities);
        }
    }
}
