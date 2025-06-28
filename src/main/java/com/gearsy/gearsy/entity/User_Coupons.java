package com.gearsy.gearsy.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Entity
@Table(name = "User_Coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User_Coupons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "couponId")
    private Coupons coupon;

    private LocalDateTime usedAt;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private Orders order;
}
