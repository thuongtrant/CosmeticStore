package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.CategoryRequest;
import com.ttt.CosmeticStore.dto.response.CategoryResponse;
import com.ttt.CosmeticStore.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
}
