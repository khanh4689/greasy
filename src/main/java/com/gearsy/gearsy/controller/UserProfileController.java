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

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final OrderService orderService;

    @GetMapping("/user/profile")
    public String viewOrEditProfile(Model model,
                                    Principal principal,
                                    @RequestParam(name = "edit", required = false) Boolean edit) {
        if (principal == null) {
            return "redirect:/login";
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
        model.addAttribute("orders", orders);
        model.addAttribute("user", user);
        model.addAttribute("title", "Thông tin cá nhân");
        model.addAttribute("contentTemplate", "/user/profile"); // dùng file đã gộp
        return "layout";
    }

    @PostMapping("/user/profile/edit")
    public String updateProfile(@ModelAttribute("userUpdateDTO") UserUpdateDTO dto,
                                Principal principal) {
        userProfileService.updateUserProfile(principal.getName(), dto);
        return "redirect:/user/profile?success";
    }

    @PostMapping("/user/profile/order/delete/{orderId}")
    public String deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrderById(orderId);
        return "redirect:/user/profile";
    }
}
