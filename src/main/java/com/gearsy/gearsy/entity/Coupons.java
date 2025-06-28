package com.gearsy.gearsy.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Entity
@Table(name = "Coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    private String code;
    private BigDecimal discountPercent;
    private BigDecimal minOrderAmount;
    private Integer maxUses;
    private Integer usedCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}
