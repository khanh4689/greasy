package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Orders;
import com.gearsy.gearsy.entity.Payments;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payments, Long> {
    Payments findByOrder(Orders order);

    // Tìm tất cả payment theo order
    List<Payments> findAllByOrder(Orders order);

    // Xóa tất cả payment theo order
    void deleteAllByOrder(Orders order);




}
