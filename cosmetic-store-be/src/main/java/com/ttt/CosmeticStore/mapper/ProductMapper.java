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

    public ProductSimpleResponse toSimpleResponse(ProductResponse productResponse) {
        if (productResponse == null) {
            return null;
        }

        ProductSimpleResponse simple = new ProductSimpleResponse();
        simple.setId(productResponse.getId());
        simple.setName(productResponse.getName());
        simple.setPrice(productResponse.getPrice());
//        simple.setCategoryName(productResponse.getCategoryName());
        simple.setIsBestSeller(productResponse.getIsBestSeller());
        simple.setIsNew(productResponse.getIsNew());

        // Use mainImage directly from ProductResponse
        simple.setMainImageUrl(productResponse.getMainImage());

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
//        if (product.getCategory() != null) {
//            simple.setCategoryName(product.getCategory().getName());
//        }

        // Use mainImageUrl directly from Product entity
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
