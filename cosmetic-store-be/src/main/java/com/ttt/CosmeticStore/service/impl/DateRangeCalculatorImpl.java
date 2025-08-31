package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.DateRange;
import com.ttt.CosmeticStore.service.DateRangeCalculator;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class DateRangeCalculatorImpl implements DateRangeCalculator {

    public DateRange calculateRange(int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        return new DateRange(startDate, endDate);
    }

    public DateRange calculateMonthRange(int months) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months);
        return new DateRange(startDate, endDate);
    }

    public DateRange calculateYearRange(int years) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusYears(years);
        return new DateRange(startDate, endDate);
    }
}
