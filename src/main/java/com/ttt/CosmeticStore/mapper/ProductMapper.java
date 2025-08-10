package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.response.ProductDetailResponse;
import com.ttt.CosmeticStore.dto.response.ProductDetailResponse;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import com.ttt.CosmeticStore.dto.response.ProductSimpleResponse;
import com.ttt.CosmeticStore.entity.Product;
import org.springframework.stereotype.Component;

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
        response.setPrice(product.getPrice());
        response.setInventory(product.getInventory());
        response.setIsBestSeller(product.getIsBestSeller());
        response.setIsNew(product.getIsNew());

        // Set category name
        if (product.getCategory() != null) {
            response.setCategoryName(product.getCategory().getName());
        }

        // Set skin types
        if (product.getSkinTypes() != null) {
            response.setSkinTypes(product.getSkinTypes().stream()
                    .map(skinType -> skinType.getName())
                    .collect(Collectors.toList()));
        }

        // Set ingredients
        if (product.getIngredients() != null) {
            response.setIngredients(product.getIngredients().stream()
                    .map(ingredient -> ingredient.getName())
                    .collect(Collectors.toList()));
        }

        // Set image URLs
        if (product.getImages() != null) {
            response.setImageUrls(product.getImages().stream()
                    .map(image -> image.getImageUrl())
                    .collect(Collectors.toList()));
        }

        return response;
    }

    public ProductSimpleResponse toSimpleResponse(ProductResponse productResponse) {
        if (productResponse == null) {
            return null;
        }

        ProductSimpleResponse simple = new ProductSimpleResponse();
        simple.setId(productResponse.getId());
        simple.setName(productResponse.getName());
        simple.setPrice(productResponse.getPrice());
        simple.setCategoryName(productResponse.getCategoryName());
        simple.setIsBestSeller(productResponse.getIsBestSeller());
        simple.setIsNew(productResponse.getIsNew());

        // Set main image (first image if available)
        if (productResponse.getImageUrls() != null && !productResponse.getImageUrls().isEmpty()) {
            simple.setMainImageUrl(productResponse.getImageUrls().get(0));
        }

        return simple;
    }

    public ProductSimpleResponse convertToSimpleResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductSimpleResponse simple = new ProductSimpleResponse();
        simple.setId(product.getId());
        simple.setName(product.getName());
        simple.setPrice(product.getPrice());
        simple.setIsBestSeller(product.getIsBestSeller());
        simple.setIsNew(product.getIsNew());

        // Set category name
        if (product.getCategory() != null) {
            simple.setCategoryName(product.getCategory().getName());
        }

        // Set main image (first image if available)
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            simple.setMainImageUrl(product.getImages().get(0).getImageUrl());
        }

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
        product.setPrice(request.getPrice());
        product.setInventory(request.getInventory());
        product.setIsBestSeller(request.getIsBestSeller() != null ? request.getIsBestSeller() : false);
        product.setIsNew(request.getIsNew() != null ? request.getIsNew() : false);

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
        product.setPrice(request.getPrice());
        product.setInventory(request.getInventory());
        product.setIsBestSeller(request.getIsBestSeller() != null ? request.getIsBestSeller() : false);
        product.setIsNew(request.getIsNew() != null ? request.getIsNew() : false);
    }

    public ProductDetailResponse toDetailResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductDetailResponse detail = new ProductDetailResponse();

        // Basic product information
        detail.setId(product.getId());
        detail.setName(product.getName());
        detail.setPrice(product.getPrice());
        detail.setDescription(product.getDescription());
        detail.setType(product.getType());
        detail.setBenefits(product.getBenefits());
        detail.setHowToUse(product.getHowToUse());
        detail.setInventory(product.getInventory());
        detail.setIsBestSeller(product.getIsBestSeller());
        detail.setIsNew(product.getIsNew());
        detail.setMainImageUrl(product.getMainImageUrl());

        // Category information
        if (product.getCategory() != null) {
            detail.setCategoryId(product.getCategory().getId());
            detail.setCategoryName(product.getCategory().getName());
        }

        // Images information
        if (product.getImages() != null) {
            detail.setImages(product.getImages().stream()
                    .map(image -> new ProductDetailResponse.ImageResponse(
                            image.getId(),
                            image.getImageUrl(),
                            image.getName(),
                            image.getType()
                    ))
                    .collect(Collectors.toList()));
        }

        // Ingredients information
        if (product.getIngredients() != null) {
            detail.setIngredients(product.getIngredients().stream()
                    .map(ingredient -> new ProductDetailResponse.IngredientResponse(
                            ingredient.getId(),
                            ingredient.getName()
                    ))
                    .collect(Collectors.toList()));
        }

        // Skin types information
        if (product.getSkinTypes() != null) {
            detail.setSkinTypes(product.getSkinTypes().stream()
                    .map(skinType -> new ProductDetailResponse.SkinTypeResponse(
                            skinType.getId(),
                            skinType.getName()
                    ))
                    .collect(Collectors.toList()));
        }

        return detail;
    }
}
