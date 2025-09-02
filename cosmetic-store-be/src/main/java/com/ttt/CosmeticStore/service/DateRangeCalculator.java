package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.DateRange;

public interface DateRangeCalculator {

     DateRange calculateRange(int days);

     DateRange calculateMonthRange(int months);

     DateRange calculateYearRange(int years);
}
