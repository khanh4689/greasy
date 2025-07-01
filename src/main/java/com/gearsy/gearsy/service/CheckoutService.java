package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.Users;

public interface CheckoutService {
    void checkoutCOD(Users user, String shippingAddress);

}
