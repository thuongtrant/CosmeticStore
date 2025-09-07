package com.ttt.CosmeticStore.validation;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.entity.Product;
import com.ttt.CosmeticStore.exception.OrderException;
import com.ttt.CosmeticStore.exception.ProductNotFoundException;
import com.ttt.CosmeticStore.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class InventoryValidator {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Validate tồn kho cho danh sách sản phẩm trong checkout
     * @param items danh sách sản phẩm cần kiểm tra
     * @throws OrderException nếu không đủ tồn kho
     */
    public void validateInventoryForCheckout(List<CheckoutRequest.CheckoutItem> items) {
        log.info("🔍 Validating inventory for {} items", items.size());

        for (CheckoutRequest.CheckoutItem item : items) {
            validateSingleProductInventory(item.getProductId(), item.getQuantity());
        }

        log.info("✅ All inventory validations passed");
    }

    /**
     * Validate tồn kho cho một sản phẩm
     * @param productId ID sản phẩm
     * @param requestedQuantity số lượng yêu cầu
     * @throws OrderException nếu không đủ tồn kho
     */
    public void validateSingleProductInventory(Long productId, Integer requestedQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (product.getInventory() < requestedQuantity) {
            log.warn("⚠️ Insufficient inventory for product {}: requested={}, available={}",
                    product.getName(), requestedQuantity, product.getInventory());
            throw new OrderException(String.format(
                "Sản phẩm '%s' chỉ còn %d sản phẩm trong kho, không đủ cho số lượng yêu cầu %d",
                product.getName(), product.getInventory(), requestedQuantity));
        }

        log.debug("✓ Inventory validation passed for product {}: requested={}, available={}",
                product.getName(), requestedQuantity, product.getInventory());
    }

    /**
     * Kiểm tra tồn kho có đủ không (không throw exception)
     * @param productId ID sản phẩm
     * @param quantity số lượng cần kiểm tra
     * @return true nếu đủ tồn kho
     */
    public boolean isInventoryAvailable(Long productId, Integer quantity) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException(productId));

            boolean available = product.getInventory() >= quantity;
            log.debug("🔍 Inventory check for product {}: requested={}, available={}, result={}",
                    productId, quantity, product.getInventory(), available);

            return available;
        } catch (Exception e) {
            log.error("Error checking inventory for product {}: {}", productId, e.getMessage());
            return false;
        }
    }
}
