package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.OrderDetails;
import com.gearsy.gearsy.entity.Orders;
import com.gearsy.gearsy.entity.Users;

import java.util.List;

public interface CheckoutService {
    void checkoutCOD(Users user, String shippingAddress);

    String createMomoPayment(Orders order);

    void processMomoReturn(Long orderId);

    Orders createOrder(Users user, String shippingAddress);
    String createVnpayPayment(Orders order);
    void processVnpayReturn(Long orderId);
    Orders getOrderById(Long orderId);
    List<OrderDetails> getOrderDetails(Orders order);
}
