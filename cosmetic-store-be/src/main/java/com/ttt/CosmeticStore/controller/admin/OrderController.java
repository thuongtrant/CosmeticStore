package com.ttt.CosmeticStore.controller.admin;

import com.ttt.CosmeticStore.dto.response.OrderResponse;
import com.ttt.CosmeticStore.dto.response.OrdersResponseA;
import com.ttt.CosmeticStore.entity.Order;
import com.ttt.CosmeticStore.service.AdminOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    @GetMapping
    public String listOrders(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String search,
                             Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OrdersResponseA> ordersPage;

        if (status != null && !status.isEmpty()) {
            ordersPage = adminOrderService.getOrdersByStatus(
                    Order.OrderStatus.valueOf(status), pageable);
        } else if (search != null && !search.trim().isEmpty()) {
                    ordersPage = adminOrderService.searchOrdersByOrderNumber(
                        search.trim(), pageable);
        } else {
            ordersPage = adminOrderService.getAllOrders(pageable);
        }

        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentSearch", search);
        model.addAttribute("orderStatuses", Order.OrderStatus.values());

        return "orders";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        try {
            OrderResponse order = adminOrderService.getOrderById(id);
            model.addAttribute("order", order);
            model.addAttribute("orderStatuses", Order.OrderStatus.values());
            return "order-detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Không tìm thấy đơn hàng");
            return "redirect:/admin/orders";
        }
    }

    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String status,
                                    RedirectAttributes redirectAttributes) {
        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status);
            adminOrderService.updateOrderStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể cập nhật trạng thái đơn hàng: " + e.getMessage());
        }

        return "redirect:/admin/orders/" + id;
    }



    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id,
                              @RequestParam(required = false) String reason,
                              RedirectAttributes redirectAttributes) {
        try {
            adminOrderService.cancelOrder(id, reason);
            redirectAttributes.addFlashAttribute("success", "Hủy đơn hàng thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể hủy đơn hàng: " + e.getMessage());
        }

        return "redirect:/admin/orders/" + id;
    }
}