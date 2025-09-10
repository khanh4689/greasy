package com.gearsy.gearsy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAdminDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private String images;
    private String compatibilitySpecs;
    private Boolean hidden;
    private Long categoryId;
    private Long supplierId;
    private String categoryName;  // thêm
    private String supplierName;  // thêm
}