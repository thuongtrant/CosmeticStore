package com.ttt.CosmeticStore.controller.admin;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import com.ttt.CosmeticStore.service.*;
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
//            if (mainImageFile != null && !mainImageFile.isEmpty()) {
                String mainImageUrl = cloudinaryService.uploadImage(mainImageFile);
                request.setMainImage(mainImageUrl);
//            }
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
            System.out.println("mainImageFile = " + (mainImageFile == null ? "null" : mainImageFile.getOriginalFilename()));
            System.out.println("imageFiles = " + (imageFiles == null ? "null" : imageFiles.length));

            productService.createProduct(request);
            return "redirect:/admin/products";
        } catch (Exception e) {
//            model.addAttribute("errorMessage", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            return "product-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        ProductResponse response = productService.getProductById(id);
        model.addAttribute("product", response);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("mode", "edit");
        return "product-form";
    }

    @PostMapping("/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute("product") ProductRequest request,
                                @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
                                @RequestParam(value = "images", required = false) MultipartFile[] images,
                                Model model) {
        if (mainImage != null && !mainImage.isEmpty()) {
            String mainImageUrl = cloudinaryService.uploadImage(mainImage);
            request.setMainImage(mainImageUrl);
        }
        List<String> imageUrls = new ArrayList<>(request.getImages() != null ? request.getImages() : List.of());
        if (images != null) {
            for (MultipartFile f : images) {
                if (f != null && !f.isEmpty()) {
                    imageUrls.add(cloudinaryService.uploadImage(f));
                }
            }
        }
        request.setImages(imageUrls);

        // 3. Gọi update
        productService.updateProduct(id, request);
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}
