package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Orders;
import com.gearsy.gearsy.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders,Long> {
    List<Orders> findAllByUser(Users user);
}
