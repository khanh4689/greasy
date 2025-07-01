package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Orders;
import com.gearsy.gearsy.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payments, Long> {
    Payments findByOrder(Orders order);


}
