package com.ttt.CosmeticStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedProductListResponse {
    private List<ProductListResponse> products;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;
    private boolean hasNext;
    private boolean hasPrevious;

}
