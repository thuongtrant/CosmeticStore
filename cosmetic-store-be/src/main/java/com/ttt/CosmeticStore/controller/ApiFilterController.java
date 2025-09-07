package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.response.CategoryResponse;
import com.ttt.CosmeticStore.dto.response.IngredientResponse;
import com.ttt.CosmeticStore.dto.response.SkinTypeResponse;
import com.ttt.CosmeticStore.entity.Category;
import com.ttt.CosmeticStore.entity.Ingredient;
import com.ttt.CosmeticStore.entity.SkinType;
import com.ttt.CosmeticStore.repository.CategoryRepository;
import com.ttt.CosmeticStore.repository.IngredientRepository;
import com.ttt.CosmeticStore.repository.SkinTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/filters")
@RequiredArgsConstructor
public class ApiFilterController {

    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final SkinTypeRepository skinTypeRepository;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryRepository.findAllByOrderByNameAsc();
        List<CategoryResponse> response = categories.stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ingredients")
    public ResponseEntity<List<IngredientResponse>> getAllIngredients() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        List<IngredientResponse> response = ingredients.stream()
                .map(this::mapToIngredientResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/skin-types")
    public ResponseEntity<List<SkinTypeResponse>> getAllSkinTypes() {
        List<SkinType> skinTypes = skinTypeRepository.findAll();
        List<SkinTypeResponse> response = skinTypes.stream()
                .map(this::mapToSkinTypeResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        return response;
    }

    private IngredientResponse mapToIngredientResponse(Ingredient ingredient) {
        IngredientResponse response = new IngredientResponse();
        response.setId(ingredient.getId());
        response.setName(ingredient.getName());
        return response;
    }

    private SkinTypeResponse mapToSkinTypeResponse(SkinType skinType) {
        SkinTypeResponse response = new SkinTypeResponse();
        response.setId(skinType.getId());
        response.setName(skinType.getName());
        return response;
    }
}
