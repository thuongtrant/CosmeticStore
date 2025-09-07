package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.entity.Order;
import com.ttt.CosmeticStore.entity.OrderItem;
import com.ttt.CosmeticStore.entity.Product;
import com.ttt.CosmeticStore.exception.OrderException;
import com.ttt.CosmeticStore.repository.ProductRepository;
import com.ttt.CosmeticStore.service.InventoryService;
import com.ttt.CosmeticStore.validation.InventoryValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryValidator inventoryValidator;

    @Override
    @Transactional
    public void validateAndReserveInventory(List<CheckoutRequest.CheckoutItem> items) {
        // Sử dụng validator để kiểm tra tồn kho
        inventoryValidator.validateInventoryForCheckout(items);
        // Note: Reserve logic có thể được thêm vào sau này nếu cần
    }

    @Override
    @Transactional
    public void confirmInventoryDeduction(Order order) {
        log.info("📦 Confirming inventory deduction for order: {}", order.getOrderNumber());

        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            int newInventory = product.getInventory() - orderItem.getQuantity();

            if (newInventory < 0) {
                log.error("❌ Negative inventory detected for product {}: current={}, deducting={}",
                        product.getName(), product.getInventory(), orderItem.getQuantity());
                throw new OrderException(String.format(
                    "Tồn kho âm cho sản phẩm '%s'. Vui lòng kiểm tra lại.", product.getName()));
            }

            product.setInventory(newInventory);
            productRepository.save(product);

            log.info("✅ Updated inventory for {}: {} -> {}",
                    product.getName(), product.getInventory() + orderItem.getQuantity(), newInventory);
        }

        log.info("✅ Inventory deduction completed for order: {}", order.getOrderNumber());
    }

    @Override
    @Transactional
    public void restoreInventory(Order order) {
        log.info("🔄 Restoring inventory for cancelled order: {}", order.getOrderNumber());

        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            int restoredInventory = product.getInventory() + orderItem.getQuantity();

            product.setInventory(restoredInventory);
            productRepository.save(product);

            log.info("✅ Restored inventory for {}: {} -> {}",
                    product.getName(), product.getInventory() - orderItem.getQuantity(), restoredInventory);
        }

        log.info("✅ Inventory restoration completed for order: {}", order.getOrderNumber());
    }

    @Override
    public boolean checkInventoryAvailability(Long productId, Integer quantity) {
        return inventoryValidator.isInventoryAvailable(productId, quantity);
    }
}
