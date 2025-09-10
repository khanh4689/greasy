package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Orders;
import com.gearsy.gearsy.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders,Long> {
    List<Orders> findAllByUser(Users user);

    @Query("SELECT SUM(o.totalAmount) FROM Orders o WHERE o.isHidden = false AND o.status = 'PAID'")
    BigDecimal getTotalRevenue();

    /**
     * Doanh thu theo tháng trong 1 năm (hiển thị biểu đồ cột)
     */
    @Query("""
        SELECT EXTRACT(MONTH FROM o.orderDate), SUM(o.totalAmount)
        FROM Orders o
        WHERE o.isHidden = false AND o.status = 'PAID' AND EXTRACT(YEAR FROM o.orderDate) = :year
        GROUP BY EXTRACT(MONTH FROM o.orderDate)
        ORDER BY EXTRACT(MONTH FROM o.orderDate)
    """)
    List<Object[]> getMonthlyRevenue(@Param("year") int year);

    /**
     * Tổng số đơn hàng hoàn tất
     */
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.isHidden = false AND o.status = 'COMPLETED'")
    Long countCompletedOrders();

    /**
     * Số lượng đơn hàng theo từng tháng trong năm
     */
    @Query("""
        SELECT EXTRACT(MONTH FROM o.orderDate), COUNT(o)
        FROM Orders o
        WHERE o.isHidden = false AND o.status = 'PAID' AND EXTRACT(YEAR FROM o.orderDate) = :year
        GROUP BY EXTRACT(MONTH FROM o.orderDate)
        ORDER BY EXTRACT(MONTH FROM o.orderDate)
    """)
    List<Object[]> getMonthlyOrderCounts(@Param("year") int year);

    /**
     * Doanh thu theo từng tuần trong 1 tháng cụ thể (PostgreSQL native query)
     */
    @Query(value = """
        SELECT EXTRACT(WEEK FROM o.order_date) AS week_number, SUM(o.total_amount)
        FROM orders o
        WHERE o.is_hidden = false AND o.status = 'COMPLETED'
          AND EXTRACT(YEAR FROM o.order_date) = :year
          AND EXTRACT(MONTH FROM o.order_date) = :month
        GROUP BY week_number
        ORDER BY week_number
        """, nativeQuery = true)
    List<Object[]> getWeeklyRevenue(@Param("year") int year, @Param("month") int month);

}
