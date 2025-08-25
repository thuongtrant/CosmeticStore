package com.ttt.CosmeticStore.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String categoryName;
    private String mainImage;
    private List<String> images;

}