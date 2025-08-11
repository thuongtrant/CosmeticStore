package com.ttt.CosmeticStore.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductSearchRequest {
    private String keyword;           // Tìm kiếm theo tên sản phẩm
    private List<Long> categoryIds;   // Lọc theo danh mục
    private List<Long> ingredientIds; // Lọc theo thành phần
    private List<Long> skinTypeIds;   // Lọc theo loại da
    private BigDecimal minPrice;      // Giá tối thiểu
    private BigDecimal maxPrice;      // Giá tối đa
    private String sortBy;            // Sắp xếp theo: price, name, createdAt
    private String sortDirection;     // ASC hoặc DESC
    private Boolean isBestSeller;     // Lọc sản phẩm bán chạy
    private Boolean isNew;            // Lọc sản phẩm mới
    private Integer page = 0;         // Trang hiện tại (bắt đầu từ 0)
    private Integer size = 20;        // Số sản phẩm mỗi trang
}
