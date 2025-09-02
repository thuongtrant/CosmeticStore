package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.dto.response.CategoryReport;
import com.ttt.CosmeticStore.dto.response.DailyReport;
import com.ttt.CosmeticStore.dto.response.ProductReport;
import com.ttt.CosmeticStore.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class ReportMapper {

    public ProductReport mapToProductReport(Object[] result) {
        if (result == null || result.length < 4) {
            return new ProductReport();
        }

        ProductReport report = new ProductReport();
        report.setProductId(((Number) result[0]).longValue());
        report.setProductName((String) result[1]);
        report.setTotalQuantity(((Number) result[2]).longValue());
        report.setTotalRevenue((BigDecimal) result[3]);

        // Set category name if available
        if (result.length > 4 && result[4] != null) {
            report.setCategoryName((String) result[4]);
        }

        // Set current stock if available
        if (result.length > 5 && result[5] != null) {
            report.setCurrentStock(((Number) result[5]).intValue());
        }

        return report;
    }

    public CategoryReport mapToCategoryReport(Object[] result) {
        if (result == null || result.length < 4) {
            return new CategoryReport();
        }

        CategoryReport report = new CategoryReport();
        report.setCategoryId(((Number) result[0]).longValue());
        report.setCategoryName((String) result[1]);
        report.setTotalQuantity(((Number) result[2]).longValue());
        report.setTotalRevenue((BigDecimal) result[3]);

        return report;
    }

    public DailyReport mapToDailyReport(Object[] result) {
        if (result == null || result.length < 2) {
            log.warn("Invalid daily report result array: length={}",
                    result != null ? result.length : 0);
            return new DailyReport();
        }

        DailyReport report = new DailyReport();

        // Enhanced date handling
        if (result[0] != null) {
            LocalDate date = null;

            if (result[0] instanceof java.sql.Date) {
                date = ((java.sql.Date) result[0]).toLocalDate();
            } else if (result[0] instanceof java.sql.Timestamp) {
                date = ((java.sql.Timestamp) result[0]).toLocalDateTime().toLocalDate();
            } else if (result[0] instanceof LocalDate) {
                date = (LocalDate) result[0];
            } else if (result[0] instanceof LocalDateTime) {
                date = ((LocalDateTime) result[0]).toLocalDate();
            } else {
                log.error("Unsupported date type in daily report: {} = {}",
                        result[0].getClass().getSimpleName(), result[0]);
                return new DailyReport(); // Return empty on error
            }

            if (date != null) {
                report.setDate(date);
                report.setFormattedDate(date.format(DateTimeFormatter.ofPattern("dd/MM")));
            }
        }

        // Handle revenue with null safety
        if (result[1] != null) {
            try {
                report.setRevenue((BigDecimal) result[1]);
            } catch (ClassCastException e) {
                log.error("Revenue casting error: {} = {}",
                        result[1].getClass().getSimpleName(), result[1]);
                report.setRevenue(BigDecimal.ZERO);
            }
        } else {
            report.setRevenue(BigDecimal.ZERO);
        }

        // Handle order count with null safety
        if (result.length > 2 && result[2] != null) {
            try {
                report.setOrderCount(((Number) result[2]).longValue());
            } catch (ClassCastException e) {
                log.error("Order count casting error: {} = {}",
                        result[2].getClass().getSimpleName(), result[2]);
                report.setOrderCount(0L);
            }
        } else {
            report.setOrderCount(0L);
        }

        return report;
    }

    public DailyReport mapToMonthlyReport(Object[] result) {
        if (result == null || result.length < 2) {
            return new DailyReport();
        }

        DailyReport report = new DailyReport();
        String monthYear = (String) result[0];
        BigDecimal revenue = (BigDecimal) result[1];

        report.setRevenue(revenue);
        report.setFormattedDate(monthYear);

        return report;
    }

    public String getStatusDisplayName(Order.OrderStatus status) {
        switch (status) {
            case PENDING:
                return "Chờ xử lý";
            case CONFIRMED:
                return "Đã xác nhận";
            case PROCESSING:
                return "Đang xử lý";
            case SHIPPED:
                return "Đã gửi";
            case DELIVERED:
                return "Đã giao";
            case CANCELLED:
                return "Đã hủy";
            default:
                return status.name();
        }
    }
}
