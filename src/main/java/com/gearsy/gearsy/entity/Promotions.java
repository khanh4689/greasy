package com.gearsy.gearsy.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Entity
@Table(name = "Promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promotionId;

    private String name;
    private BigDecimal discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}

