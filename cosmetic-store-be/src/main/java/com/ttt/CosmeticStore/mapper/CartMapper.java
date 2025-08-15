package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.dto.response.CartResponse;
import com.ttt.CosmeticStore.entity.Cart;
import com.ttt.CosmeticStore.entity.CartItem;
import com.ttt.CosmeticStore.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartResponse toCartResponse(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());

        // User information
        if (cart.getUser() != null) {
            response.setUserId(cart.getUser().getId());
            response.setUsername(cart.getUser().getUsername());
        }

        // Cart items
        if (cart.getCartItems() != null) {
            response.setCartItems(cart.getCartItems().stream()
                    .map(this::toCartItemResponse)
                    .collect(Collectors.toList()));

            // Calculate totals
            BigDecimal totalAmount = cart.getCartItems().stream()
                    .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Integer totalQuantity = cart.getCartItems().stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();

            response.setTotalAmount(totalAmount);
            response.setTotalQuantity(totalQuantity);
        } else {
            response.setTotalAmount(BigDecimal.ZERO);
            response.setTotalQuantity(0);
        }

        return response;
    }

    // CartMapper.java - thêm null check và xử lý lazy loading
    private CartResponse.CartItemResponse toCartItemResponse(CartItem cartItem) {
        if (cartItem == null || cartItem.getProduct() == null) {
            return null;
        }

        CartResponse.CartItemResponse itemResponse = new CartResponse.CartItemResponse();
        itemResponse.setId(cartItem.getId());
        itemResponse.setQuantity(cartItem.getQuantity());
        itemResponse.setCreatedAt(cartItem.getCreatedAt());

        Product product = cartItem.getProduct();
        itemResponse.setProductId(product.getId());
        itemResponse.setProductName(product.getName());
        itemResponse.setProductPrice(product.getPrice());
        itemResponse.setProductImageUrl(product.getMainImageUrl());

        // Safe category access
        try {
            if (product.getCategory() != null) {
                itemResponse.setCategoryName(product.getCategory().getName());
            }
        } catch (Exception e) {
            // Handle lazy loading exception
            itemResponse.setCategoryName(null);
        }

        // Stock information
        itemResponse.setInStock(product.getInventory() != null && product.getInventory() > 0);
        itemResponse.setAvailableQuantity(product.getInventory() != null ? product.getInventory() : 0);

        // Calculate subtotal
        if (product.getPrice() != null) {
            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            itemResponse.setSubtotal(subtotal);
        }

        return itemResponse;
    }
}
