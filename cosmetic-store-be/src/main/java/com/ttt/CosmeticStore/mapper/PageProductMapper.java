package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.dto.response.*;
import com.ttt.CosmeticStore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class PageProductMapper {
    public <T> PagedResponse<T> toPagedResponse(List<T> content, Page<?> page) {
        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
    public <T> PagedResponse<T> toEmptyPagedResponse(Page<?> page) {
        return new PagedResponse<>(
                new ArrayList<>(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
