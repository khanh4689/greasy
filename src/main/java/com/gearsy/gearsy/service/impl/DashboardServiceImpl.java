package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.repository.OrderRepository;
import com.gearsy.gearsy.repository.UsersRepository;
import com.gearsy.gearsy.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository ordersRepository;
    private final UsersRepository usersRepository;

    @Override
    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = Optional.ofNullable(ordersRepository.getTotalRevenue()).orElse(BigDecimal.ZERO);
        System.out.println("Raw Revenue from DB: " + ordersRepository.getTotalRevenue()); // Log giá trị thô
        System.out.println("Total Revenue after handling: " + revenue); // Log giá trị sau khi xử lý
        return revenue;
    }

    @Override
    public Long getTotalUsers() {
        return usersRepository.countActiveUsers();
    }

    @Override
    public Map<Integer, BigDecimal> getMonthlyRevenue(int year) {
        List<Object[]> results = ordersRepository.getMonthlyRevenue(year);
        Map<Integer, BigDecimal> monthlyRevenue = new HashMap<>();

        for (Object[] row : results) {
            Integer month = ((Number) row[0]).intValue();
            BigDecimal amount = (BigDecimal) row[1];
            monthlyRevenue.put(month, amount);
        }
        for (int i = 1; i <= 12; i++) {
            monthlyRevenue.putIfAbsent(i, BigDecimal.ZERO);
        }
        return monthlyRevenue;
    }

    @Override
    public Map<Integer, Long> getMonthlyRegisteredUsers(int year) {
        List<Object[]> results = usersRepository.countRegisteredUsersByMonth(year);
        Map<Integer, Long> monthlyUsers = new HashMap<>();

        for (Object[] row : results) {
            Integer month = ((Number) row[0]).intValue();
            Long count = ((Number) row[1]).longValue();
            monthlyUsers.put(month, count);
        }
        for (int i = 1; i <= 12; i++) {
            monthlyUsers.putIfAbsent(i, 0L);
        }
        return monthlyUsers;
    }

    @Override
    public Map<Integer, BigDecimal> getWeeklyRevenue(int year, int month) {
        List<Object[]> results = ordersRepository.getWeeklyRevenue(year, month);
        Map<Integer, BigDecimal> weeklyRevenue = new HashMap<>();

        for (Object[] row : results) {
            Integer weekOfYear = ((Number) row[0]).intValue();
            BigDecimal amount = (BigDecimal) row[1];
            weeklyRevenue.put(weekOfYear, amount);
        }
        return weeklyRevenue;
    }
}

