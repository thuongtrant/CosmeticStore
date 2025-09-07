package com.ttt.CosmeticStore.controller.admin;

import com.ttt.CosmeticStore.dto.DateRange;
import com.ttt.CosmeticStore.dto.response.ReportSummary;
import com.ttt.CosmeticStore.service.ChartDataService;
import com.ttt.CosmeticStore.service.DateRangeCalculator;
import com.ttt.CosmeticStore.service.ReportDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportDataService reportDataService;
    private final ChartDataService chartDataService;
    private final DateRangeCalculator dateCalculator;

    @GetMapping("/reports")
    public String showReportsPage(Model model,
                                  @RequestParam(value = "days", defaultValue = "7") int days) {

        try {
            // Calculate date range
            DateRange range = dateCalculator.calculateRange(days);

            ReportSummary summary = reportDataService.getReportSummary(range.getStart(), range.getEnd());

            model.addAttribute("selectedDays", days);
            model.addAttribute("summary", summary);
            model.addAttribute("totalRevenue", summary.getTotalRevenue());
            model.addAttribute("totalOrders", summary.getTotalOrders());
            model.addAttribute("totalUsers", summary.getTotalUsers());
            model.addAttribute("totalProducts", summary.getTotalProducts());
            model.addAttribute("ordersByStatus", summary.getOrdersByStatus());
            model.addAttribute("topProducts", summary.getTopProducts());
            model.addAttribute("categoryStats", summary.getCategoryStats());
            model.addAttribute("dailyRevenue", summary.getDailyRevenue());
            model.addAttribute("newCustomers", summary.getNewCustomers());
            model.addAttribute("activeCustomers", summary.getActiveCustomers());
            model.addAttribute("lowStockCount", summary.getLowStockCount());
            model.addAttribute("outOfStockProducts", summary.getOutOfStockProducts());


        } catch (Exception e) {
            addDefaultAttributes(model, days);
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

            return chartDataService.getRevenueChartData(startDate, endDate);
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
            DateRange range = dateCalculator.calculateRange(days);
            return chartDataService.getCategoryChartData(range.getStart(), range.getEnd());
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
            DateRange range = dateCalculator.calculateRange(days);
            return chartDataService.getOrderStatusChartData(range.getStart(), range.getEnd());
        } catch (Exception e) {
            log.error("Lỗi khi lấy dữ liệu biểu đồ trạng thái đơn hàng: ", e);
            return Map.of("labels", List.of(), "data", List.of());
        }
    }

    private void addDefaultAttributes(Model model, int days) {
        model.addAttribute("selectedDays", days);
        model.addAttribute("totalRevenue", java.math.BigDecimal.ZERO);
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
    }
}
