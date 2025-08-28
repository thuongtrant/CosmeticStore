package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.dto.response.*;
import com.ttt.CosmeticStore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class PageProductMapper {
    public PagedProductByTypeResponse toPagedProductResponse(List<ProductByTypeResponse> products, Page<Product> productPage) {
        PagedProductByTypeResponse response = new PagedProductByTypeResponse();
        response.setProducts(products);
        response.setCurrentPage(productPage.getNumber());
        response.setTotalPages(productPage.getTotalPages());
        response.setTotalElements(productPage.getTotalElements());
        response.setSize(productPage.getSize());
        response.setHasNext(productPage.hasNext());
        response.setHasPrevious(productPage.hasPrevious());
        return response;
    }
    public PagedProductListResponse toPagedProductListResponse(List<ProductListResponse> products, Page<ProductBasicInfo> page) {
        PagedProductListResponse response = new PagedProductListResponse();
        response.setProducts(products);
        response.setCurrentPage(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setSize(page.getSize());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }
    public PagedProductListResponse toEmptyPagedProductListResponse(Page<ProductBasicInfo> page) {
        PagedProductListResponse response = new PagedProductListResponse();
        response.setProducts(new ArrayList<>());
        response.setCurrentPage(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setSize(page.getSize());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }
}
