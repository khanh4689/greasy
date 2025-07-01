package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.entity.*;
import com.gearsy.gearsy.repository.OrderDetailsRepository;
import com.gearsy.gearsy.repository.OrderRepository;
import com.gearsy.gearsy.repository.PaymentRepository;
import com.gearsy.gearsy.repository.ProductsRepository;
import com.gearsy.gearsy.service.CartService;
import com.gearsy.gearsy.service.CheckoutService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final OrderRepository orderRepo;
    private final OrderDetailsRepository orderDetailsRepo;
    private final PaymentRepository paymentRepo;
    private final CartService cartService;
    private final ProductsRepository productRepo;


    @Override
    public void checkoutCOD(Users user, String shippingAddress) {
        List<Cart_Items> cartItems = cartService.getCartItems(user);
        BigDecimal total = cartItems.stream()
                .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Orders order = new Orders(user, total, LocalDateTime.now(), "PROCESSING",
                shippingAddress, BigDecimal.valueOf(20000), false, LocalDateTime.now(), LocalDateTime.now());
        order = orderRepo.save(order);

        for (Cart_Items item : cartItems) {
            orderDetailsRepo.save(new OrderDetails(null, order, item.getProduct(),
                    item.getQuantity(), item.getProduct().getPrice()));

            // ✅ Cập nhật tồn kho
            Products product = item.getProduct();
            int stock = product.getStock() - item.getQuantity();
            if (stock < 0) throw new IllegalStateException("Không đủ hàng tồn");
            product.setStock(stock);
            product.setUpdatedAt(LocalDateTime.now());
            productRepo.save(product);
        }

        Payments payment = new Payments(null, order, "COD", LocalDateTime.now(), "PENDING");
        paymentRepo.save(payment);
        cartService.clearCart(user);
    }


}
