package com.gearsy.gearsy.controller;

import com.gearsy.gearsy.dto.UserUpdateDTO;
import com.gearsy.gearsy.entity.Orders;
import com.gearsy.gearsy.entity.Users;

import com.gearsy.gearsy.service.OrderService;
import com.gearsy.gearsy.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final OrderService orderService;


    /**
     * Trang hồ sơ cá nhân (xem / chỉnh sửa)
     */
    @GetMapping("/profile")
    public String viewOrEditProfile(Model model,
                                    Principal principal,
                                    @RequestParam(name = "edit", required = false) Boolean edit,
                                    @RequestParam(name = "success", required = false) String success,
                                    @RequestParam(name = "error", required = false) String error) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        String email = principal.getName();
        Users user = userProfileService.getUserByEmail(email);
        List<Orders> orders = orderService.getOrdersByUserEmail(email);

        model.addAttribute("editMode", edit != null && edit);

        if (Boolean.TRUE.equals(edit)) {
            UserUpdateDTO dto = new UserUpdateDTO();
            dto.setName(user.getName());
            dto.setPhone(user.getPhone());
            dto.setAddress(user.getAddress());
            model.addAttribute("userUpdateDTO", dto);
        }

        if (success != null) {
            model.addAttribute("message", success);
        }
        if (error != null) {
            model.addAttribute("error", error);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("user", user);
        model.addAttribute("title", "Thông tin cá nhân");
        model.addAttribute("contentTemplate", "/user/profile"); // layout sẽ load file con
        return "layout";
    }

    /**
     * Cập nhật thông tin hồ sơ
     */
    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute("userUpdateDTO") UserUpdateDTO dto,
                                Principal principal)
    { if (principal == null) {

        return "redirect:/auth/login";

    } userProfileService.updateUserProfile(principal.getName(), dto);
        return "redirect:/user/profile?success";
    }
    @PostMapping("/profile/order/delete/{orderId}")
    public String deleteOrder(@PathVariable Long orderId, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        orderService.deleteOrderById(orderId);
        return "redirect:/user/profile?success=Đã xóa đơn hàng";
    }
}
