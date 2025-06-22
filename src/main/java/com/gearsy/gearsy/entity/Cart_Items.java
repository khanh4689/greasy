package com.gearsy.gearsy.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Cart_Items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart_Items {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne
    @JoinColumn(name = "cartId")
    private Carts cart;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Products product;

    private Integer quantity;
}
