package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.dto.request.ProductRequest;
import com.ttt.CosmeticStore.dto.response.ProductResponse;
import com.ttt.CosmeticStore.entity.Image;
import com.ttt.CosmeticStore.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setInventory(request.getInventory());
        product.setType(request.getType());
        product.setBenefits(request.getBenefits());
        product.setHowToUse(request.getHowToUse());
        product.setIsBestSeller(request.getIsBestSeller() != null ? request.getIsBestSeller() : false);
        product.setIsNew(request.getIsNew() != null ? request.getIsNew() : false);
        product.setMainImageUrl(request.getMainImage());

        // mapping list url → list Image
        if (request.getImages() != null) {
            List<Image> imageEntities = request.getImages().stream()
                    .map(url -> {
                        Image img = new Image();
                        img.setImageUrl(url);
                        img.setProduct(product);
                        return img;
                    }).collect(Collectors.toList());
            product.setImages(imageEntities);
        }
        return product;
    }

    public ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setInventory(product.getInventory());
        response.setType(product.getType());
        response.setBenefits(product.getBenefits());
        response.setHowToUse(product.getHowToUse());
        response.setIsBestSeller(product.getIsBestSeller());
        response.setIsNew(product.getIsNew());
        response.setMainImage(product.getMainImageUrl());

        // mapping list Image → list url
        if (product.getImages() != null) {
            List<String> imageUrls = product.getImages().stream()
                    .map(Image::getImageUrl)
                    .collect(Collectors.toList());
            response.setImages(imageUrls);
        }
        if (product.getCategory() != null) {
            response.setCategoryName(product.getCategory().getName());
        }
        return response;
    }

    // update dữ liệu, dùng cho updateProduct
    public void updateEntity(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setInventory(request.getInventory());
        product.setType(request.getType());
        product.setBenefits(request.getBenefits());
        product.setHowToUse(request.getHowToUse());
        product.setIsBestSeller(request.getIsBestSeller() != null ? request.getIsBestSeller() : false);
        product.setIsNew(request.getIsNew() != null ? request.getIsNew() : false);
        product.setMainImageUrl(request.getMainImage());
        // update images sẽ làm ở service (xóa ảnh cũ, thêm mới ảnh)
    }
}
