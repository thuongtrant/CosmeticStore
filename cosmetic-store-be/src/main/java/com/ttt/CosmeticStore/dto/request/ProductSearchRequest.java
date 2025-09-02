package com.ttt.CosmeticStore.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductSearchRequest {
    private String keyword;
    private List<Long> categoryIds;
    private List<Long> ingredientIds;
    private List<Long> skinTypeIds;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sortBy;
    private String sortDirection;
    private Boolean isBestSeller;
    private Boolean isNew;
    private Integer page = 0;
    private Integer size = 20;
}
