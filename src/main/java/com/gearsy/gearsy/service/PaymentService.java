package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.Orders;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    String processPayment(Long orderId, String method);
    String createMomoPayment(Orders order);
}