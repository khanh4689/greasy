package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.Cart_Items;
import com.gearsy.gearsy.entity.Users;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    void addToCart(Users user, Long productId, int quantity);
    List<Cart_Items> getCartItems(Users user);
    void updateQuantity(Long cartItemId, int quantity);
    void removeItem(Long cartItemId);

    BigDecimal calculateTotal(Users user);


    void clearCart(Users user);
}

