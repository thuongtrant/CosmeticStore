package com.ttt.CosmeticStore.controller.admin;

import com.ttt.CosmeticStore.dto.response.ProductStatisticResponse;
import com.ttt.CosmeticStore.dto.response.RevenueStatisticResponse;
import com.ttt.CosmeticStore.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/reports")
    public String showReportsPage(Model model,
                                  @RequestParam(value = "startDate", required = false) String startDateStr,
                                  @RequestParam(value = "endDate", required = false) String endDateStr,
                                  @RequestParam(value = "period", defaultValue = "day") String period) {

        // Xử lý ngày mặc định (30 ngày gần đây)
        LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : LocalDate.now();
        LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : endDate.minusDays(30);

        try {
            // Lấy thống kê doanh thu
            List<RevenueStatisticResponse> revenueStats = reportService.getRevenueStatistics(startDate, endDate, period);

            // Lấy thống kê sản phẩm
            List<ProductStatisticResponse> topSellingProducts = reportService.getTopSellingProducts(startDate, endDate, 10);
            List<ProductStatisticResponse> lowStockProducts = reportService.getLowStockProducts(10);

            // Tính tổng các chỉ số
            BigDecimal totalRevenue = reportService.getTotalRevenue(startDate, endDate);
            Long totalOrders = reportService.getTotalOrders(startDate, endDate);
            Long totalCustomers = reportService.getTotalCustomers(startDate, endDate);

            // Tính doanh thu trung bình mỗi đơn hàng
            BigDecimal avgOrderValue = totalOrders > 0 ?
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) :
                BigDecimal.ZERO;

            // Thêm dữ liệu vào model
            model.addAttribute("revenueStats", revenueStats);
            model.addAttribute("topSellingProducts", topSellingProducts);
            model.addAttribute("lowStockProducts", lowStockProducts);
            model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
            model.addAttribute("totalOrders", totalOrders != null ? totalOrders : 0L);
            model.addAttribute("totalCustomers", totalCustomers != null ? totalCustomers : 0L);
            model.addAttribute("avgOrderValue", avgOrderValue);
            model.addAttribute("startDate", startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            model.addAttribute("endDate", endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            model.addAttribute("period", period);

        } catch (Exception e) {
            // Xử lý lỗi và hiển thị dữ liệu mặc định
            model.addAttribute("error", "Có lỗi xảy ra khi tải dữ liệu báo cáo: " + e.getMessage());
            model.addAttribute("revenueStats", List.of());
            model.addAttribute("topSellingProducts", List.of());
            model.addAttribute("lowStockProducts", List.of());
            model.addAttribute("totalRevenue", BigDecimal.ZERO);
            model.addAttribute("totalOrders", 0L);
            model.addAttribute("totalCustomers", 0L);
            model.addAttribute("avgOrderValue", BigDecimal.ZERO);
            model.addAttribute("startDate", startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            model.addAttribute("endDate", endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            model.addAttribute("period", period);
        }

        return "admin-reports";
    }
}
