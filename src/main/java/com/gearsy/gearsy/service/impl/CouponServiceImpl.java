package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.entity.Coupons;
import com.gearsy.gearsy.entity.User_Coupons;
import com.gearsy.gearsy.entity.Users;
import com.gearsy.gearsy.repository.UserCouponRepository;
import com.gearsy.gearsy.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final UserCouponRepository userCouponsRepo;

    public List<User_Coupons> getAvailableCoupons(Users user) {
        return userCouponsRepo.findByUserAndUsedAtIsNull(user);
    }

    public BigDecimal applyCoupon(BigDecimal total, Coupons coupon) {

        if (coupon.getStartDate().isAfter(LocalDateTime.now()) ||
                coupon.getEndDate().isBefore(LocalDateTime.now())) {
            return total;
        }

        // kiểm tra min order
        if (total.compareTo(coupon.getMinOrderAmount()) < 0) {
            return total;
        }

        BigDecimal discount = total.multiply(coupon.getDiscountPercent().divide(BigDecimal.valueOf(100)));
        return total.subtract(discount);
    }
}
