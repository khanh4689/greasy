package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.Coupons;
import com.gearsy.gearsy.entity.User_Coupons;
import com.gearsy.gearsy.entity.Users;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    List<User_Coupons> getAvailableCoupons(Users user);
    BigDecimal applyCoupon(BigDecimal total, Coupons coupon);
}
