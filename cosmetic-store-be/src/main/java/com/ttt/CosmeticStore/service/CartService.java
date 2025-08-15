package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.AddToCartRequest;
import com.ttt.CosmeticStore.dto.response.CartResponse;

public interface CartService {
    CartResponse addToCart(Long userId, AddToCartRequest request);
    CartResponse getCartByUserId(Long userId);
    CartResponse updateCartItemQuantity(Long userId, Long productId, Integer quantity);
    void removeFromCart(Long userId, Long productId);
    void clearCart(Long userId);
    Integer getCartItemCount(Long userId);
}
