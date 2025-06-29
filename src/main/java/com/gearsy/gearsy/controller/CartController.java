package com.gearsy.gearsy.controller;

import org.springframework.ui.Model;
import com.gearsy.gearsy.entity.Cart_Items;
import com.gearsy.gearsy.entity.Users;
import com.gearsy.gearsy.service.AuthService;
import com.gearsy.gearsy.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final AuthService userService;

    @GetMapping
    public String viewCart(Model model, Principal principal) {
        Users user = userService.getUserByEmail(principal.getName());
        List<Cart_Items> items = cartService.getCartItems(user);

        BigDecimal total = items.stream()
                .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("items", items);
        model.addAttribute("title", "Giỏ hàng của bạn");
        model.addAttribute("total", total);
        return "cart/view";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            Principal principal) {
        Users user = userService.getUserByEmail(principal.getName());
        cartService.addToCart(user, productId, quantity);
        return "redirect:/user/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long cartItemId,
                                 @RequestParam int quantity) {
        cartService.updateQuantity(cartItemId, quantity);
        return "redirect:/user/cart";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam Long cartItemId) {
        cartService.removeItem(cartItemId);
        return "redirect:/user/cart";
    }
}
