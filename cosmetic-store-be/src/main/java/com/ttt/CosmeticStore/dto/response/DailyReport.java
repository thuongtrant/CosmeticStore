package com.ttt.CosmeticStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyReport {
    private LocalDate date;
    private String formattedDate;
    private BigDecimal revenue;
    private Long orderCount;
    private Long customerCount;
}
