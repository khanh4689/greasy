package com.gearsy.gearsy.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Entity
@Table(name = "Banners")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Banners {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bannerId;

    private String imageURL;
    private String content;
    private String position;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
