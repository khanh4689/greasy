package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.entity.*;
import com.gearsy.gearsy.repository.*;
import com.gearsy.gearsy.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository ordersRepo;
    private final ProductsRepository productsRepo;
    private final OrderDetailsRepository orderDetailsRepo;
    private final PaymentRepository paymentsRepo;
    private final CartItemRepository cartItemRepository;

    @Transactional
    @Override
    public void processCODPayment(Long orderId) {
        Orders order = ordersRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payments payment = new Payments();
        payment.setOrder(order);
        payment.setPaymentMethod("COD");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus("PAID");
        paymentsRepo.save(payment);

        updateProductStock(order);
        order.setStatus("PAID");
        ordersRepo.save(order);
    }

    @Override
    @Transactional
    public void updateProductStock(Orders order) {
        List<OrderDetails> orderItems = orderDetailsRepo.findByOrder(order);
        for (OrderDetails item : orderItems) {
            Products product = item.getProduct();
            if (product.getStock() >= item.getQuantity()) {
                product.setStock(product.getStock() - item.getQuantity());
                productsRepo.save(product);
            } else {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }
        }
    }

    @Override
    public Orders createOrderFromCart(Users user, List<Cart_Items> cartItems, String shippingAddress, BigDecimal totalAmount) {
        // 1️⃣ Tạo đơn hàng
        Orders order = new Orders();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING"); // mặc định
        order.setShippingFee(BigDecimal.ZERO); // hoặc mặc định theo quận/huyện
        order.setIsHidden(false);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Orders savedOrder = ordersRepo.save(order);

        // 2️⃣ Tạo chi tiết đơn hàng
        for (Cart_Items item : cartItems) {
            OrderDetails detail = new OrderDetails();
            detail.setOrder(savedOrder);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getProduct().getPrice());

            orderDetailsRepo.save(detail);
        }

        // 3️⃣ Xoá giỏ hàng sau khi tạo đơn
        cartItemRepository.deleteAll(cartItems);

        return savedOrder;
    }
}

