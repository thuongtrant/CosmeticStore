package com.ttt.CosmeticStore.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class PagedProductResponse {
    private List<ProductSimpleResponse> products;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;
    private boolean hasNext;
    private boolean hasPrevious;
}
