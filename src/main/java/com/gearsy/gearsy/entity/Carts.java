package com.gearsy.gearsy.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Entity
@Table(name = "Carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToOne
    @JoinColumn(name = "userId")
    private Users user;

    private LocalDateTime createdAt;
}

