package com.ttt.CosmeticStore.controller;


import com.ttt.CosmeticStore.entity.Category;
import com.ttt.CosmeticStore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final CategoryService categoryService;


    @GetMapping("/category")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "category";
    }

    @GetMapping("/category/new")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "category-form";
    }

    @PostMapping("/category")
    public String addCategory(@ModelAttribute Category category) {
        categoryService.saveCategory(category);
        return "redirect:/admin/category";
    }

    @GetMapping("/category/{id}/edit")
    public String showEditCategoryForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id).orElse(null);
        if (category != null) {
            model.addAttribute("category", category);
            return "category-form";
        }
        return "redirect:/admin/category-form";
    }

    @PostMapping("/category/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category) {
        category.setId(id);
        categoryService.saveCategory(category);
        return "redirect:/admin/category";
    }

    @GetMapping("/category/{id}/delete")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/category";
    }


}
