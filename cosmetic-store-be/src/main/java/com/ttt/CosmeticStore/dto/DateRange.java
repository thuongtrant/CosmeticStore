package com.ttt.CosmeticStore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateRange {
    private LocalDateTime start;
    private LocalDateTime end;

    public LocalDate getStartDate() {
        return start.toLocalDate();
    }

    public LocalDate getEndDate() {
        return end.toLocalDate();
    }
}
