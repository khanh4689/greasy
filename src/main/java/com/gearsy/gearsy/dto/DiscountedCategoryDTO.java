package com.gearsy.gearsy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DiscountedCategoryDTO {
    private Long categoryId;
    private String categoryName;
    private BigDecimal maxDiscountToday;
    private LocalDateTime endDate;
    private String images;

    public Date getEndDateAsDate() {
        return Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    public String getEndDateAsString() {
        return endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }
}

