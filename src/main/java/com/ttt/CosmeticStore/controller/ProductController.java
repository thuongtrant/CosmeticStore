package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.request.CategoryRequest;
import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import com.ttt.CosmeticStore.service.CategoryService;
import com.ttt.CosmeticStore.service.ProductService;
import com.ttt.CosmeticStore.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CloudinaryService cloudinaryService;
    private final CategoryService categoryService;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "product";
    }
    @PostMapping("/test-upload")
    @ResponseBody
    public String testUpload(@RequestParam("files") MultipartFile[] files) {
        return "So file: " + files.length;
    }

    @GetMapping("/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new ProductRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("mode", "create");
        return "product-form";
    }

    @PostMapping
    public String createProduct(@ModelAttribute("product") ProductRequest request,
                                @RequestParam("mainImageFile") MultipartFile mainImageFile,
                                @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
                                Model model) {
        try {
            // upload main image
            if (mainImageFile != null && !mainImageFile.isEmpty()) {
                String mainImageUrl = cloudinaryService.uploadImage(mainImageFile);
                request.setMainImage(mainImageUrl);
            }
            // upload images phụ
            List<String> imageUrls = new ArrayList<>();
            if (imageFiles != null) {
                for (MultipartFile image : imageFiles) {
                    if (image != null && !image.isEmpty()) {
                        String url = cloudinaryService.uploadImage(image);
                        imageUrls.add(url);
                    }
                }
            }
            request.setImages(imageUrls);

            // Xử lý isBestSeller, isNew
            if (request.getIsBestSeller() == null) request.setIsBestSeller(false);
            if (request.getIsNew() == null) request.setIsNew(false);

            productService.createProduct(request);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            return "product-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        ProductResponse response = productService.getProductById(id);
        model.addAttribute("product", response);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("mode", "edit");
        return "product-form";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute("product") ProductRequest request,
                                @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
                                @RequestParam(value = "images", required = false) MultipartFile[] images,
                                Model model) {
        try {
            // upload main image nếu có upload mới
            if (mainImage != null && !mainImage.isEmpty()) {
                String mainImageUrl = cloudinaryService.uploadImage(mainImage);
                request.setMainImage(mainImageUrl);
            }
            // upload images phụ nếu có
            List<String> imageUrls = new ArrayList<>();
            if (images != null) {
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        String url = cloudinaryService.uploadImage(image);
                        imageUrls.add(url);
                    }
                }
            }
            if (!imageUrls.isEmpty()) request.setImages(imageUrls);

            // Xử lý isBestSeller, isNew
            if (request.getIsBestSeller() == null) request.setIsBestSeller(false);
            if (request.getIsNew() == null) request.setIsNew(false);

            productService.updateProduct(id, request);
            return "redirect:/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            return "product-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}
