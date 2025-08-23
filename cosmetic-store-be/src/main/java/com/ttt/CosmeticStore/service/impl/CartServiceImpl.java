package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.request.AddToCartRequest;
import com.ttt.CosmeticStore.dto.response.CartResponse;
import com.ttt.CosmeticStore.entity.Cart;
import com.ttt.CosmeticStore.entity.CartItem;
import com.ttt.CosmeticStore.entity.Product;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.exception.ResourceNotFoundException;
import com.ttt.CosmeticStore.mapper.CartMapper;
import com.ttt.CosmeticStore.repository.CartItemRepository;
import com.ttt.CosmeticStore.repository.CartRepository;
import com.ttt.CosmeticStore.repository.ProductRepository;
import com.ttt.CosmeticStore.repository.UserRepository;
import com.ttt.CosmeticStore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    // CartServiceImpl.java - Thêm try-catch và flush
    @Override
    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        System.out.println("🛒 CartService - addToCart started with userId: " + userId + ", productId: " + request.getProductId() + ", quantity: " + request.getQuantity());

        try {
            // Validate user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id: " + userId));
            System.out.println("✅ User found: " + user.getUsername());

            // Validate product exists
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id: " + request.getProductId()));
            System.out.println("✅ Product found: " + product.getName() + ", inventory: " + product.getInventory());

            // Check inventory availability
            if (product.getInventory() == null || product.getInventory() < request.getQuantity()) {
                throw new IllegalArgumentException("Số lượng sản phẩm không đủ. Còn lại: " +
                        (product.getInventory() != null ? product.getInventory() : 0));
            }

            // Find or create cart for user
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        System.out.println("📦 Creating new cart for user: " + userId);
                        Cart newCart = new Cart();
                        newCart.setUser(user);
                        return cartRepository.saveAndFlush(newCart); // Use saveAndFlush
                    });
            System.out.println("🛒 Cart found/created with id: " + cart.getId());

            // Check if product already exists in cart
            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId());

            if (existingItem.isPresent()) {
                System.out.println("📦 Product already in cart, updating quantity");
                CartItem cartItem = existingItem.get();
                int newQuantity = cartItem.getQuantity() + request.getQuantity();

                if (product.getInventory() < newQuantity) {
                    throw new IllegalArgumentException("Số lượng sản phẩm không đủ. Còn lại: " + product.getInventory());
                }

                cartItem.setQuantity(newQuantity);
                cartItemRepository.saveAndFlush(cartItem);
                System.out.println("✅ Cart item updated with new quantity: " + newQuantity);
            } else {
                System.out.println("📦 Adding new product to cart");
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setProduct(product);
                cartItem.setQuantity(request.getQuantity());
                cartItemRepository.saveAndFlush(cartItem);
                System.out.println("✅ New cart item created");
            }

            // Return updated cart
            System.out.println("🔍 Fetching updated cart with items...");
            Cart updatedCart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng"));
            System.out.println("✅ Updated cart fetched with " +
                    (updatedCart.getCartItems() != null ? updatedCart.getCartItems().size() : 0) + " items");

            CartResponse response = cartMapper.toCartResponse(updatedCart);
            System.out.println("✅ CartResponse created successfully");
            return response;

        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            System.out.println("🚨 Business error in CartService.addToCart: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("🚨 Unexpected error in CartService.addToCart: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng", e);
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

        Product product = cartItem.getProduct();
        if (product.getInventory() < quantity) {
            throw new IllegalArgumentException("Số lượng sản phẩm không đủ. Còn lại: " + product.getInventory());
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        Cart updatedCart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ h��ng"));

        return cartMapper.toCartResponse(updatedCart);
    }

    @Override
    public void removeFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng"));

        cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);
    }

    @Override
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
