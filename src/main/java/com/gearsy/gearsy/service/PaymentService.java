package com.gearsy.gearsy.service;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    String processPayment(Long orderId, String method);
}