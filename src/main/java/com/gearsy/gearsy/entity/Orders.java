package com.gearsy.gearsy.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;

    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private String status;
    private String shippingAddress;
    private BigDecimal shippingFee;
    private Boolean isHidden;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Orders(Users user, BigDecimal totalAmount, LocalDateTime orderDate, String status,
                  String shippingAddress, BigDecimal shippingFee, Boolean isHidden,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.shippingFee = shippingFee;
        this.isHidden = isHidden;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}