package com.gearsy.gearsy.controller;

import com.gearsy.gearsy.entity.Users;
import com.gearsy.gearsy.service.AuthService;
import com.gearsy.gearsy.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final AuthService userService;

    @PostMapping("/checkout")
    public String handleCheckout(@RequestParam String shippingAddress,
                                 @RequestParam String paymentMethod,
                                 Principal principal) {
        Users user = userService.getUserByEmail(principal.getName());
        if (paymentMethod.equals("COD")) {
            checkoutService.checkoutCOD(user, shippingAddress);
            return "redirect:/user/cart";
        }
        return "redirect:/user/cart?error";
    }

}

