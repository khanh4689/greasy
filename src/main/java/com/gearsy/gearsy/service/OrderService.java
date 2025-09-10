package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.Cart_Items;
import com.gearsy.gearsy.entity.Orders;
import com.gearsy.gearsy.entity.Users;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    void processCODPayment(Long orderId);
    void updateProductStock(Orders order);
    Orders createOrderFromCart(Users user, List<Cart_Items> cartItems, String fullAddress, BigDecimal total);
    List<Orders> getOrdersByUserEmail(String email);
    List<Orders> getAllOrders();

    void deleteOrderById(Long id);
    Orders hideOrder(Long orderId); // Ẩn đơn
    Orders changeOrderStatus(Long orderId, String status); // Đổi trạng thái
}
