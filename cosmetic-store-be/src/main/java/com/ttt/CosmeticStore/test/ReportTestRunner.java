//package com.ttt.CosmeticStore.test;
//
//import com.ttt.CosmeticStore.service.ReportService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//
//@Component
//@Slf4j
//public class ReportTestRunner implements CommandLineRunner {
//
//    @Autowired
//    private ReportService reportService;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Test báo cáo thống kê với dữ liệu 30 ngày gần đây
//        LocalDate endDate = LocalDate.now();
//        LocalDate startDate = endDate.minusDays(30);
//
//        log.info("=== TEST BÁO CÁO THỐNG KÊ ===");
//        log.info("Khoảng thời gian: {} - {}", startDate, endDate);
//
//        try {
//            // Test tổng doanh thu
//            var totalRevenue = reportService.getTotalRevenue(startDate, endDate);
//            log.info("✅ Tổng doanh thu: {} VNĐ", totalRevenue);
//
//            // Test tổng đơn hàng
//            var totalOrders = reportService.getTotalOrders(startDate, endDate);
//            log.info("✅ Tổng đơn hàng: {}", totalOrders);
//
//            // Test tổng khách hàng
//            var totalCustomers = reportService.getTotalCustomers(startDate, endDate);
//            log.info("✅ Tổng khách hàng: {}", totalCustomers);
//
//            // Test giá trị trung bình
//            var avgOrderValue = reportService.getAverageOrderValue(startDate, endDate);
//            log.info("✅ Giá trị TB/đơn: {} VNĐ", avgOrderValue);
//
//            // Test thống kê doanh thu
//            var revenueStats = reportService.getRevenueStatistics(startDate, endDate, "day");
//            log.info("✅ Thống kê doanh thu: {} bản ghi", revenueStats.size());
//
//            // Test top sản phẩm bán chạy
//            var topProducts = reportService.getTopSellingProducts(startDate, endDate, 5);
//            log.info("✅ Top sản phẩm bán chạy: {} sản phẩm", topProducts.size());
//
//            // Test sản phẩm tồn kho thấp
//            var lowStockProducts = reportService.getLowStockProducts(5);
//            log.info("✅ Sản phẩm tồn kho thấp: {} sản phẩm", lowStockProducts.size());
//
//            log.info("=== TEST HOÀN THÀNH THÀNH CÔNG ===");
//
//        } catch (Exception e) {
//            log.error("❌ Lỗi khi test báo cáo thống kê: ", e);
//        }
//    }
//}


