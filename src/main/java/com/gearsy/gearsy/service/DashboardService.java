package com.gearsy.gearsy.service;

import java.math.BigDecimal;
import java.util.Map;

public interface DashboardService {
    /**
     * Tổng doanh thu từ tất cả các đơn hàng hoàn tất và không ẩn
     */
    BigDecimal getTotalRevenue();

    /**
     * Tổng số người dùng đã đăng ký (có thể dùng thêm điều kiện active nếu muốn)
     */
    Long getTotalUsers();

    /**
     * Doanh thu theo tháng trong một năm cụ thể
     * Trả về Map<Tháng (1-12), Tổng doanh thu>
     */
    Map<Integer, BigDecimal> getMonthlyRevenue(int year);

    /**
     * Số người dùng đăng ký theo từng tháng trong năm
     * Trả về Map<Tháng (1-12), Số lượng người dùng>
     */
    Map<Integer, Long> getMonthlyRegisteredUsers(int year);

    /**
     * Doanh thu theo từng tuần trong một tháng cụ thể
     * Trả về Map<Tuần, Doanh thu>
     */
    Map<Integer, BigDecimal> getWeeklyRevenue(int year, int month);
}
