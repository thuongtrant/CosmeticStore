package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.request.AddToCartRequest;
import com.ttt.CosmeticStore.dto.response.CartResponse;
import com.ttt.CosmeticStore.entity.*;
import com.ttt.CosmeticStore.exception.ResourceNotFoundException;
import com.ttt.CosmeticStore.mapper.CartMapper;
import com.ttt.CosmeticStore.repository.*;
import com.ttt.CosmeticStore.service.CartService;
import com.ttt.CosmeticStore.validation.InventoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final InventoryValidator inventoryValidator;

    @Override
    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        System.out.println("🛒 CartService - addToCart started with userId: " + userId + ", productId: " + request.getProductId() + ", quantity: " + request.getQuantity());

        try {
            // Validate user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id: " + userId));

            // Validate product exists
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id: " + request.getProductId()));

            inventoryValidator.validateSingleProductInventory(request.getProductId(), request.getQuantity());

            // Find or create cart for user
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        System.out.println("📦 Creating new cart for user: " + userId);
                        Cart newCart = new Cart();
                        newCart.setUser(user);
                        return cartRepository.saveAndFlush(newCart);
                    });

            // Check if product already exists in cart
            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId());

            if (existingItem.isPresent()) {
                CartItem cartItem = existingItem.get();
                int newQuantity = cartItem.getQuantity() + request.getQuantity();

                // Validate tổng sl
                inventoryValidator.validateSingleProductInventory(request.getProductId(), newQuantity);

                cartItem.setQuantity(newQuantity);
                cartItemRepository.saveAndFlush(cartItem);
            } else {
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setProduct(product);
                cartItem.setQuantity(request.getQuantity());
                cartItemRepository.saveAndFlush(cartItem);
            }

            // Return updated cart
            CartResponse response = getCartByUserId(userId);
            return response;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart == null) {
            // Create empty cart for user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id: " + userId));

            Cart newCart = new Cart();
            newCart.setUser(user);
            cart = cartRepository.save(newCart);
        }

        return cartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng"));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));

        //kiểm tra tồn kho
        inventoryValidator.validateSingleProductInventory(productId, quantity);

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        Cart updatedCart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng"));

        return cartMapper.toCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng"));

        cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng"));

        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getCartItemCount(Long userId) {
        return cartItemRepository.countItemsByUserId(userId);
    }
}
