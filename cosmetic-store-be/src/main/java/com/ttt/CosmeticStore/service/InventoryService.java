package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;
import com.ttt.CosmeticStore.entity.Order;

import java.util.List;

public interface InventoryService {
    /**
     * Kiểm tra và giữ chỗ tồn kho cho các sản phẩm trong checkout request
     * @param items danh sách sản phẩm cần kiểm tra
     * @throws com.ttt.CosmeticStore.exception.OrderException nếu không đủ tồn kho
     */
    void validateAndReserveInventory(List<CheckoutRequest.CheckoutItem> items);

    /**
     * Xác nhận trừ tồn kho khi đơn hàng thành công
     * @param order đơn hàng đã được tạo
     */
    void confirmInventoryDeduction(Order order);

    /**
     * Hoàn trả tồn kho khi đơn hàng bị hủy
     * @param order đơn hàng bị hủy
     */
    void restoreInventory(Order order);

    /**
     * Kiểm tra tồn kho cho một sản phẩm
     * @param productId ID sản phẩm
     * @param quantity số lượng cần kiểm tra
     * @return true nếu đủ tồn kho
     */
    boolean checkInventoryAvailability(Long productId, Integer quantity);
}
