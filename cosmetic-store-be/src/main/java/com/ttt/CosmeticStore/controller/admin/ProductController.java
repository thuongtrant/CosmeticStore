package com.ttt.CosmeticStore.controller.admin;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.response.PagedResponse;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import com.ttt.CosmeticStore.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Thêm annotation này
public class ProductController {

    private final ProductService productService;
    private final CloudinaryService cloudinaryService;
    private final CategoryService categoryService;
    private final IngredientService ingredientService;
    private final SkinTypeService skinTypeService;

    @GetMapping
    public String listProducts(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) {
        // Sử dụng method phân trang mới thay vì getAllProductsForList
        PagedResponse pagedProducts = productService.getProducts(page, size);
        model.addAttribute("pagedProducts", pagedProducts);
        model.addAttribute("products", pagedProducts.getProducts());
        return "product";
    }


    @GetMapping("/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new ProductRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("ingredients", ingredientService.getAllIngredients());
        model.addAttribute("skinTypes", skinTypeService.getAllSkinTypes());
        model.addAttribute("mode", "create");
        return "product-form";
    }

    @PostMapping
    public String createProduct(@ModelAttribute("product") ProductRequest request,
                                @RequestParam("mainImageFile") MultipartFile mainImageFile,
                                @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
                                Model model) {
        try {
                String mainImageUrl = cloudinaryService.uploadImage(mainImageFile);
                request.setMainImage(mainImageUrl);
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
        model.addAttribute("ingredients", ingredientService.getAllIngredients());
        model.addAttribute("skinTypes", skinTypeService.getAllSkinTypes());
        model.addAttribute("mode", "edit");
        return "product-form";
    }

    @PostMapping("/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute("product") ProductRequest request,
                                @RequestParam(value = "mainImageFile", required = false) MultipartFile mainImageFile,
                                @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
                                @RequestParam(value = "deleteMainImage", required = false) String deleteMainImage,
                                @RequestParam(value = "deletedImages", required = false) String deletedImages,
                                Model model) {
        try {
            // Lấy thông tin sản phẩm hiện tại để giữ lại hình ảnh cũ
            ProductResponse currentProduct = productService.getProductById(id);

            // Xử lý hình ảnh chính
            if ("true".equals(deleteMainImage)) {
                // Nếu xóa hình ảnh chính và có upload hình mới
                if (mainImageFile != null && !mainImageFile.isEmpty()) {
                    String mainImageUrl = cloudinaryService.uploadImage(mainImageFile);
                    request.setMainImage(mainImageUrl);
                } else {
                    // Nếu xóa nhưng không upload hình mới, bắt buộc phải có hình
                    model.addAttribute("errorMessage", "Bạn phải chọn hình ảnh chính mới khi xóa hình ảnh hiện tại!");
                    model.addAttribute("categories", categoryService.getAllCategories());
                    model.addAttribute("ingredients", ingredientService.getAllIngredients());
                    model.addAttribute("skinTypes", skinTypeService.getAllSkinTypes());
                    model.addAttribute("mode", "edit");
                    return "product-form";
                }
            } else if (mainImageFile != null && !mainImageFile.isEmpty()) {
                // Thay đổi hình ảnh chính
                String mainImageUrl = cloudinaryService.uploadImage(mainImageFile);
                request.setMainImage(mainImageUrl);
            } else {
                // Giữ lại hình ảnh cũ
                request.setMainImage(currentProduct.getMainImage());
            }

            // Xử lý hình ảnh phụ
            List<String> imageUrls = new ArrayList<>();

            // Lọc ra những ảnh cũ không bị xóa
            if (currentProduct.getImages() != null) {
                List<String> deletedImagesList = new ArrayList<>();
                if (deletedImages != null && !deletedImages.isEmpty()) {
                    deletedImagesList = Arrays.asList(deletedImages.split(","));
                }

                for (String existingImage : currentProduct.getImages()) {
                    if (!deletedImagesList.contains(existingImage)) {
                        imageUrls.add(existingImage);
                    }
                }
            }

            // Thêm hình ảnh phụ mới nếu có
            if (imageFiles != null) {
                for (MultipartFile file : imageFiles) {
                    if (file != null && !file.isEmpty()) {
                        String imageUrl = cloudinaryService.uploadImage(file);
                        imageUrls.add(imageUrl);
                    }
                }
            }
            request.setImages(imageUrls);

            // Xử lý isBestSeller, isNew
            if (request.getIsBestSeller() == null) request.setIsBestSeller(false);
            if (request.getIsNew() == null) request.setIsNew(false);

            // Cập nhật sản phẩm
            productService.updateProduct(id, request);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("ingredients", ingredientService.getAllIngredients());
            model.addAttribute("skinTypes", skinTypeService.getAllSkinTypes());
            model.addAttribute("mode", "edit");
            return "product-form";
        }
    }

    // Thêm method GET cho delete để hỗ trợ link từ HTML
    @GetMapping("/{id}/delete")
    public String deleteProductConfirm(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }
}
