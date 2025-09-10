package com.gearsy.gearsy.config;

import com.gearsy.gearsy.entity.Users;
import com.gearsy.gearsy.service.CartService;
import com.gearsy.gearsy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@Component
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final CartService cartService;
    private final UserService userService;

    /**
     * Thêm số lượng sản phẩm trong giỏ hàng vào tất cả model
     */
    @ModelAttribute
    public void addCartCount(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Nếu đã login
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            Users user = userService.getUserByEmail(email);

            if (user != null) {
                int count = cartService.countItems(user); // tổng số lượng sản phẩm trong giỏ
                model.addAttribute("cartCount", count);
            } else {
                // user không tồn tại trong DB
                model.addAttribute("cartCount", 0);
            }
        } else {
            // Chưa login
            model.addAttribute("cartCount", 0);
        }
    }
}
