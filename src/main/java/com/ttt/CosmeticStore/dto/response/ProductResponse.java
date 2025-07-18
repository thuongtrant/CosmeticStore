package com.ttt.CosmeticStore.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String name;
    private String type;
    private String benefits;
    private String description;
    private String howToUse;
    private BigDecimal price;
    private Integer inventory;
    private Boolean isBestSeller;
    private Boolean isNew;
    private String categoryName;
}
