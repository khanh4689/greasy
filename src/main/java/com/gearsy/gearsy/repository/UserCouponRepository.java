package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.User_Coupons;
import com.gearsy.gearsy.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCouponRepository extends JpaRepository<User_Coupons, Long> {
    List<User_Coupons> findByUserAndUsedAtIsNull(Users user);
}

