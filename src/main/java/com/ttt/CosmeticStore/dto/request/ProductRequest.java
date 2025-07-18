package com.ttt.CosmeticStore.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {
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
    private Long categoryId;
}
