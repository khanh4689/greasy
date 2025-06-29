package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.entity.Cart_Items;
import com.gearsy.gearsy.entity.Carts;
import com.gearsy.gearsy.entity.Products;
import com.gearsy.gearsy.entity.Users;
import com.gearsy.gearsy.repository.CartItemRepository;
import com.gearsy.gearsy.repository.CartRepository;
import com.gearsy.gearsy.repository.ProductsRepository;
import com.gearsy.gearsy.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductsRepository productRepository;

    @Override
    public void addToCart(Users user, Long productId, int quantity) {
        Carts cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Carts newCart = new Carts();
                    newCart.setUser(user);
                    newCart.setCreatedAt(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart_Items item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseGet(() -> new Cart_Items(null, cart, product, 0));
        item.setQuantity(item.getQuantity() + quantity);
        cartItemRepository.save(item);
    }

    @Override
    public List<Cart_Items> getCartItems(Users user) {
        Carts cart = cartRepository.findByUser(user).orElseThrow(); // lấy giỏ hàng theo user
        return cartItemRepository.findByCartWithProduct(cart); // ⚠️ gọi method mới
    }

    @Override
    public void updateQuantity(Long cartItemId, int quantity) {
        Cart_Items item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Override
    public void removeItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}

