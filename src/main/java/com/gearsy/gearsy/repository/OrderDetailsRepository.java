package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.OrderDetails;
import com.gearsy.gearsy.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    List<OrderDetails> findByOrder(Orders order);
}
