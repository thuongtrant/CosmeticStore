package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.entity.Image;
import com.ttt.CosmeticStore.entity.Product;
import com.ttt.CosmeticStore.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ImageService {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ImageRepository imageRepository;

    public String uploadMainImage(MultipartFile file) {
        validateImageFile(file);
        return cloudinaryService.uploadImage(file);
    }

    public void uploadAdditionalImages(List<MultipartFile> files, Product product) {
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                validateImageFile(file);
                String imageUrl = cloudinaryService.uploadImage(file);
                Image image = new Image();
                image.setImageUrl(imageUrl);
                image.setProduct(product);
                imageRepository.save(image);
            }
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được để trống");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File phải là định dạng ảnh (jpg, png, ...)");
        }
    }
}