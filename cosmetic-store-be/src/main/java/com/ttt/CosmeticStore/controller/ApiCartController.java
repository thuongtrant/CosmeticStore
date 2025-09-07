package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.request.AddToCartRequest;
import com.ttt.CosmeticStore.dto.response.CartResponse;
import com.ttt.CosmeticStore.service.CartService;
import com.ttt.CosmeticStore.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class ApiCartController {

    private final CartService cartService;
    private final UserServiceImpl userService;

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody AddToCartRequest request) {

        try {
            Long userId = getCurrentUserId();
            CartResponse response = cartService.addToCart(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        Long userId = getCurrentUserId();
        CartResponse response = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemCount() {
        Long userId = getCurrentUserId();
        Integer count = cartService.getCartItemCount(userId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        Long userId = getCurrentUserId();
        CartResponse response = cartService.updateCartItemQuantity(userId, productId, quantity);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long productId) {
        Long userId = getCurrentUserId();
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        Long userId = getCurrentUserId();
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getUserByUsername(username).getId();
    }
}
