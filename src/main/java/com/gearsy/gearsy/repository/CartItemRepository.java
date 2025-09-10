package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Cart_Items;
import com.gearsy.gearsy.entity.Carts;
import com.gearsy.gearsy.entity.Products;
import com.gearsy.gearsy.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<Cart_Items, Long> {

    // 👉 JOIN FETCH product để tránh lỗi null trong Thymeleaf
    @Query("SELECT ci FROM Cart_Items ci JOIN FETCH ci.product WHERE ci.cart = :cart")
    List<Cart_Items> findByCartWithProduct(@Param("cart") Carts cart);

    Optional<Cart_Items> findByCartAndProduct(Carts cart, Products product);
    List<Cart_Items> findByCartUser(Users user);

    void deleteByCart_User(Users user);
    List<Cart_Items> findByCart(Carts cart);
    @Query("select coalesce(sum(ci.quantity), 0) from Cart_Items ci where ci.cart.user = :user")
    Long sumQuantityByUser(@Param("user") Users user);

    List<Cart_Items> findByCart_User(Users user);


}
