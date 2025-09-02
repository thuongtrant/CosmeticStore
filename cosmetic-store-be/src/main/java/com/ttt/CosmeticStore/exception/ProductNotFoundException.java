package com.ttt.CosmeticStore.exception;

public class ProductNotFoundException extends OrderException {
    public ProductNotFoundException(Long productId) {
        super("Không tìm thấy sản phẩm với ID: " + productId);
    }
}


