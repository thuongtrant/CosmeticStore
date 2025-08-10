package com.ttt.CosmeticStore.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String type;
    private String benefits;
    private String howToUse;
    private Integer inventory;
    private Boolean isBestSeller;
    private Boolean isNew;

    // Category information
    private Long categoryId;
    private String categoryName;

    // Main image
    private String mainImageUrl;

    // All images
    private List<ImageResponse> images;

    // Ingredients
    private List<IngredientResponse> ingredients;

    // Skin types
    private List<SkinTypeResponse> skinTypes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageResponse {
        private Long id;
        private String imageUrl;
        private String name;
        private String type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientResponse {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkinTypeResponse {
        private Long id;
        private String name;
    }
}
