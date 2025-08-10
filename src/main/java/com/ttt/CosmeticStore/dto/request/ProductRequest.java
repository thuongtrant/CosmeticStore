package com.ttt.CosmeticStore.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer inventory;
    private String type;
    private String benefits;
    private String howToUse;
    private Boolean isBestSeller;
    private Boolean isNew;
    private Long categoryId;
    private String mainImage;
    private List<String> images;
    private List<Long> ingredientIds;
    private List<Long> skinTypeIds;
}
