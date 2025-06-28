package com.gearsy.gearsy.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Entity
@Table(name = "Product_Promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPromotions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productPromotionId;

    @ManyToOne
    @JoinColumn(name = "productid")
    private Products product;

    @ManyToOne
    @JoinColumn(name = "promotionid")
    private Promotions promotion;
}
