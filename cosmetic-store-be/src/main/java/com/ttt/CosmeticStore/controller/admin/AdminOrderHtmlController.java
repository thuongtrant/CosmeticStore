//package com.ttt.CosmeticStore.controller.admin;
//
//import com.ttt.CosmeticStore.entity.Order;
//import com.ttt.CosmeticStore.repository.OrderRepository;
//import com.ttt.CosmeticStore.service.OrderService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//@RequestMapping("/admin/orders")
//@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminOrderHtmlController {
//
//    private final OrderRepository orderRepository;
//    private final OrderService orderService;
//
//    @GetMapping
//    public String listOrders(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) String status,
//            Model model) {
//
//        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
//        Page<Order> orders;
//
//        if (status != null && !status.isEmpty() && !status.equals("ALL")) {
//            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
//            orders = orderRepository.findByStatus(orderStatus, pageRequest);
//        } else {
//            orders = orderRepository.findAll(pageRequest);
//        }
//
//        // Thống kê đơn hàng
//        long totalOrders = orderRepository.count();
//        long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
//        long confirmedOrders = orderRepository.countByStatus(Order.OrderStatus.CONFIRMED);
//        long shippedOrders = orderRepository.countByStatus(Order.OrderStatus.SHIPPED);
//        long deliveredOrders = orderRepository.countByStatus(Order.OrderStatus.DELIVERED);
//        long cancelledOrders = orderRepository.countByStatus(Order.OrderStatus.CANCELLED);
//
//        model.addAttribute("orders", orders.getContent());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", orders.getTotalPages());
//        model.addAttribute("totalElements", orders.getTotalElements());
//        model.addAttribute("selectedStatus", status != null ? status : "ALL");
//
//        // Thống kê
//        model.addAttribute("totalOrders", totalOrders);
//        model.addAttribute("pendingOrders", pendingOrders);
//        model.addAttribute("confirmedOrders", confirmedOrders);
//        model.addAttribute("shippedOrders", shippedOrders);
//        model.addAttribute("deliveredOrders", deliveredOrders);
//        model.addAttribute("cancelledOrders", cancelledOrders);
//
//        // Các trạng thái có thể chọn
//        model.addAttribute("orderStatuses", Order.OrderStatus.values());
//
//        return "orders";
//    }
//
//    @GetMapping("/{id}")
//    public String viewOrderDetail(@PathVariable Long id, Model model) {
//        Order order = orderRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
//
//        model.addAttribute("order", order);
//        model.addAttribute("orderStatuses", Order.OrderStatus.values());
//
//        return "order-detail";
//    }
//
//    @PostMapping("/{id}/update-status")
//    public String updateOrderStatus(
//            @PathVariable Long id,
//            @RequestParam String status,
//            RedirectAttributes redirectAttributes) {
//        try {
//            Order order = orderRepository.findById(id)
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
//
//            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
//            order.setStatus(newStatus);
//            orderRepository.save(order);
//
//            redirectAttributes.addFlashAttribute("successMessage",
//                "Cập nhật trạng thái đơn hàng thành công!");
//
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage",
//                "Lỗi khi cập nhật trạng thái: " + e.getMessage());
//        }
//
//        return "redirect:/admin/orders/" + id;
//    }
//
//    @PostMapping("/{id}/delete")
//    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        try {
//            orderRepository.deleteById(id);
//            redirectAttributes.addFlashAttribute("successMessage", "Xóa đơn hàng thành công!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage",
//                "Lỗi khi xóa đơn hàng: " + e.getMessage());
//        }
//
//        return "redirect:/admin/orders";
//    }
//}
