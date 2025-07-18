//package com.ttt.CosmeticStore.controller;
//
//import com.cloudinary.Cloudinary;
//import com.ttt.CosmeticStore.dto.request.ProductRequest;
//import com.ttt.CosmeticStore.entity.Category;
//import com.ttt.CosmeticStore.entity.Image;
//import com.ttt.CosmeticStore.entity.Product;
//import com.ttt.CosmeticStore.repository.CategoryRepository;
//import com.ttt.CosmeticStore.repository.ImageRepository;
//import com.ttt.CosmeticStore.service.ProductService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//@Controller
//@RequestMapping("/admin/product")
//@RequiredArgsConstructor
//public class ProductController {
//    private final ProductService productService;
//    private final CategoryRepository categoryRepository;
//    private final ImageRepository imageRepository;
//    private final Cloudinary cloudinary;
//
//    @GetMapping
//    public String listProducts(Model model) {
//        List<Product> products = productService.getAllProducts();
//        model.addAttribute("products", products);
//        return "product";
//    }
//
////    @GetMapping("/new")
////    public String createForm(Model model) {
////        model.addAttribute("productRequest", new ProductRequest());
////        model.addAttribute("categories", categoryRepository.findAll());
////        return "product-form";
////    }
//
//    @PostMapping("/save")
//    public String saveProduct(@ModelAttribute ProductRequest productRequest,
//                              @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
//        productService.createProduct(productRequest, imageFile);
//        return "redirect:/admin/product";
//    }
//
////
////    @GetMapping("/new")
////    public String showAddProductForm(Model model) {
////        model.addAttribute("productRequest", new ProductRequest());
////        model.addAttribute("categories", categoryRepository.findAll());
////        return "product-form";
////    }
//
//    @GetMapping("/new")
//    public String showAddProductForm(Model model) {
//        model.addAttribute("productRequest", new ProductRequest());
//        List<Category> categories = categoryRepository.findAll();
//        model.addAttribute("categories", categories);
//        if (categories.isEmpty()) {
//            model.addAttribute("error", "Chưa có danh mục nào, vui lòng thêm danh mục trước.");
//        }
//        return "product-form";
//    }
//    @GetMapping("/edit/{id}")
//    public String editForm(@PathVariable Long id, Model model) {
//        Product product = productService.getProductById(id).orElseThrow();
//        ProductRequest dto = productService.mapToRequest(product);
//        model.addAttribute("productRequest", dto);
//        model.addAttribute("categories", categoryRepository.findAll());
//        return "product-form";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String delete(@PathVariable Long id) {
//        productService.deleteProduct(id);
//        return "redirect:/admin/product";
//    }
//}

package com.ttt.CosmeticStore.controller;

import com.cloudinary.Cloudinary;
import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.entity.Category;
import com.ttt.CosmeticStore.entity.Image;
import com.ttt.CosmeticStore.entity.Product;
import com.ttt.CosmeticStore.repository.CategoryRepository;
import com.ttt.CosmeticStore.repository.ImageRepository;
import com.ttt.CosmeticStore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final Cloudinary cloudinary;

    @GetMapping
    public String listProducts(Model model) {
        try {
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products);
            return "product";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách sản phẩm");
            return "product";
        }
    }

    @GetMapping("/new")
    public String showAddProductForm(Model model) {
        try {
            model.addAttribute("productRequest", new ProductRequest());
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);

            if (categories.isEmpty()) {
                model.addAttribute("error", "Chưa có danh mục nào, vui lòng thêm danh mục trước khi thêm sản phẩm.");
            }

            return "product-form";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải form thêm sản phẩm");
            return "redirect:/admin/product";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        try {
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            ProductRequest dto = productService.mapToRequest(product);
            dto.setId(id);

            model.addAttribute("productRequest", dto);
            model.addAttribute("categories", categoryRepository.findAll());

            return "product-form";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải form sửa sản phẩm");
            return "redirect:/admin/product";
        }
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute ProductRequest productRequest,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              Model model) {
        try {
            // Kiểm tra danh mục có tồn tại không
            if (productRequest.getCategoryId() == null) {
                model.addAttribute("error", "Vui lòng chọn danh mục");
                model.addAttribute("categories", categoryRepository.findAll());
                return "product-form";
            }

                productService.createProduct(productRequest, imageFile);


            return "redirect:/admin/product";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi lưu sản phẩm: " + e.getMessage());
            model.addAttribute("categories", categoryRepository.findAll());
            return "product-form";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        try {
            productService.deleteProduct(id);
            return "redirect:/admin/product";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi xóa sản phẩm");
            return "redirect:/admin/product";
        }
    }
}