package com.ttt.CosmeticStore.controller;


import com.ttt.CosmeticStore.dto.request.CategoryRequest;
import com.ttt.CosmeticStore.dto.response.CategoryResponse;
import com.ttt.CosmeticStore.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "category";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("category")) {
            model.addAttribute("category", new CategoryRequest());
        }
        return "category-form";
    }


    @PostMapping
    public String createCategory(@Valid @ModelAttribute("category") CategoryRequest request,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "category-form";
        }
        categoryService.createCategory(request);
        return "redirect:/admin/categories";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        CategoryResponse category = categoryService.getCategoryById(id);
        model.addAttribute("category", category);
        return "category-form";
    }

    @PostMapping("/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("category") CategoryRequest request,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "category-form";
        }
        categoryService.updateCategory(id, request);
        return "redirect:/admin/categories";
    }

    @GetMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }


}
