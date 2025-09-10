package com.gearsy.gearsy.controller.admin;

import com.gearsy.gearsy.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String showOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/orders/list";
    }

    @PostMapping("/{id}/hide")
    public String hideOrder(@PathVariable Long id) {
        orderService.hideOrder(id);
        return "redirect:/admin/orders";
    }

    @PostMapping("/{orderId}/delete")
    public String deleteOrder(@PathVariable Long orderId, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrderById(orderId); // service xử lý logic xóa
            redirectAttributes.addFlashAttribute("success", "Đã xóa đơn hàng thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xóa thất bại: " + e.getMessage());
        }
        return "redirect:/admin/orders"; // quay lại trang danh sách
    }

    @PostMapping("/{id}/update-status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.changeOrderStatus(id,status);
        return "redirect:/admin/orders";
    }
}
