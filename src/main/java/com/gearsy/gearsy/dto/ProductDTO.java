package com.gearsy.gearsy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String image;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private boolean onSale;
    private LocalDateTime createdAt;
}
