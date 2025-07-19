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
    private Boolean isBestSeller; // mặc định null → xử lý ở service nếu null thì false
    private Boolean isNew;        // tương tự
    private Long categoryId;
    private String mainImage;         // url ảnh đại diện
    private List<String> images;      // list url ảnh phụ
}
