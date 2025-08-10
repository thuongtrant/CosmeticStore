package com.ttt.CosmeticStore.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String benefits;
    private String howToUse;
    private BigDecimal price;
    private Integer inventory;
    private Boolean isBestSeller;
    private Boolean isNew;
    private String categoryName;
    private List<String> skinTypes;
    private List<String> ingredients;
    private List<String> imageUrls;
}
