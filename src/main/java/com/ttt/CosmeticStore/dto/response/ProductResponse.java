package com.ttt.CosmeticStore.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer inventory;
    private String type;
    private String benefits;
    private String howToUse;
    private Boolean isBestSeller;
    private Boolean isNew;
    private String mainImage;
    private List<String> images;
    private String categoryName;
}
