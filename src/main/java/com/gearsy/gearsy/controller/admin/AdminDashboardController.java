package com.gearsy.gearsy.controller.admin;

import com.gearsy.gearsy.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin/dashboard")
    public String showDashboard(Model model) {
        int year = LocalDate.now().getYear();

        Map<Integer, BigDecimal> monthlyRevenue = dashboardService.getMonthlyRevenue(year);

        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("totalUsers", dashboardService.getTotalUsers());
        model.addAttribute("totalRevenue", dashboardService.getTotalRevenue());

        return "admin/dashboard"; // trả về file dashboard.html
    }
}
