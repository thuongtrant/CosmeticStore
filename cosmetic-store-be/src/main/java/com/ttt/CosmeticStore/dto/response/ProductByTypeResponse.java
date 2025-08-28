package com.ttt.CosmeticStore.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductByTypeResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Boolean isBestSeller;
    private Boolean isNew;
    private String mainImageUrl;
}
