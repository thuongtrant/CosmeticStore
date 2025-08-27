package com.ttt.CosmeticStore.controller.admin;

import com.ttt.CosmeticStore.dto.response.CategoryReport;
import com.ttt.CosmeticStore.dto.response.DailyReport;
import com.ttt.CosmeticStore.dto.response.ProductReport;
import com.ttt.CosmeticStore.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/reports")
    public String showReportsPage(Model model,
                                  @RequestParam(value = "days", defaultValue = "7") int days) {

        log.info("=== HIỂN THỊ TRANG BÁO CÁO THỐNG KÊ ===");
        log.info("Số ngày thống kê: {}", days);

        try {
            // Tính khoảng thời gian
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(days);
            LocalDate startLocalDate = startDate.toLocalDate();
            LocalDate endLocalDate = endDate.toLocalDate();

            // Lấy thống kê tổng quan
            BigDecimal totalRevenue = reportService.getTotalRevenue(startDate, endDate);
            Long totalOrders = reportService.getTotalOrders(startDate, endDate);
            Long totalUsers = reportService.getTotalUsers();
            Long totalProducts = reportService.getTotalProducts();

            log.info("Thống kê tổng quan - Doanh thu: {}, Đơn hàng: {}, Users: {}, Products: {}",
                     totalRevenue, totalOrders, totalUsers, totalProducts);

            // Lấy thống kê chi tiết
            Map<String, Long> ordersByStatus = reportService.getOrdersByStatus(startDate, endDate);
            List<ProductReport> topProducts = reportService.getTopSellingProducts(startDate, endDate, 10);
            List<CategoryReport> categoryStats = reportService.getCategoryStats(startDate, endDate);
            List<DailyReport> dailyRevenue = reportService.getDailyRevenue(startLocalDate, endLocalDate);

            log.info("Thống kê chi tiết - Top products: {}, Categories: {}, Daily revenue: {}",
                     topProducts.size(), categoryStats.size(), dailyRevenue.size());

            // Thêm dữ liệu vào model
            model.addAttribute("selectedDays", days);
            model.addAttribute("totalRevenue", totalRevenue);
            model.addAttribute("totalOrders", totalOrders);
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("ordersByStatus", ordersByStatus);
            model.addAttribute("topProducts", topProducts);
            model.addAttribute("categoryStats", categoryStats);
            model.addAttribute("dailyRevenue", dailyRevenue);

            // Thống kê khách hàng
            Long newCustomers = reportService.getNewCustomers(startDate, endDate);
            Long activeCustomers = reportService.getActiveCustomers(startDate, endDate);
            model.addAttribute("newCustomers", newCustomers);
            model.addAttribute("activeCustomers", activeCustomers);

            // Thống kê kho hàng
            Long lowStockCount = reportService.getLowStockProducts(10);
            List<ProductReport> outOfStockProducts = reportService.getOutOfStockProducts();
            model.addAttribute("lowStockCount", lowStockCount);
            model.addAttribute("outOfStockProducts", outOfStockProducts);

            log.info("✅ Báo cáo thống kê được tải thành công");

        } catch (Exception e) {
            log.error("❌ Lỗi khi tải báo cáo thống kê: ", e);

            // Thêm dữ liệu mặc định khi có lỗi
            model.addAttribute("selectedDays", days);
            model.addAttribute("totalRevenue", BigDecimal.ZERO);
            model.addAttribute("totalOrders", 0L);
            model.addAttribute("totalUsers", 0L);
            model.addAttribute("totalProducts", 0L);
            model.addAttribute("ordersByStatus", Map.of());
            model.addAttribute("topProducts", List.of());
            model.addAttribute("categoryStats", List.of());
            model.addAttribute("dailyRevenue", List.of());
            model.addAttribute("newCustomers", 0L);
            model.addAttribute("activeCustomers", 0L);
            model.addAttribute("lowStockCount", 0L);
            model.addAttribute("outOfStockProducts", List.of());

            model.addAttribute("error", "Có lỗi xảy ra khi tải dữ liệu báo cáo: " + e.getMessage());
        }

        return "reports";
    }

    @GetMapping("/reports/revenue-chart")
    @ResponseBody
    public Map<String, Object> getRevenueChartData(
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr) {

        try {
            LocalDate startDate = startDateStr != null ?
                LocalDate.parse(startDateStr) : LocalDate.now().minusDays(7);
            LocalDate endDate = endDateStr != null ?
                LocalDate.parse(endDateStr) : LocalDate.now();

            return reportService.getRevenueChartData(startDate, endDate);
        } catch (Exception e) {
            log.error("Lỗi khi lấy dữ liệu biểu đồ doanh thu: ", e);
            return Map.of("labels", List.of(), "data", List.of());
        }
    }

    @GetMapping("/reports/category-chart")
    @ResponseBody
    public Map<String, Object> getCategoryChartData(
            @RequestParam(value = "days", defaultValue = "30") int days) {

        try {
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(days);

            return reportService.getCategoryChartData(startDate, endDate);
        } catch (Exception e) {
            log.error("Lỗi khi lấy dữ liệu biểu đồ danh mục: ", e);
            return Map.of("labels", List.of(), "data", List.of());
        }
    }

    @GetMapping("/reports/order-status-chart")
    @ResponseBody
    public Map<String, Object> getOrderStatusChartData(
            @RequestParam(value = "days", defaultValue = "30") int days) {

        try {
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(days);

            return reportService.getOrderStatusChartData(startDate, endDate);
        } catch (Exception e) {
            log.error("Lỗi khi lấy dữ liệu biểu đồ trạng thái đơn hàng: ", e);
            return Map.of("labels", List.of(), "data", List.of());
        }
    }
}
