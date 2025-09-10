package com.gearsy.gearsy.controller;

import com.gearsy.gearsy.entity.Coupons;
import com.gearsy.gearsy.entity.User_Coupons;
import com.gearsy.gearsy.repository.UserCouponRepository;
import com.gearsy.gearsy.service.CouponService;
import org.springframework.ui.Model;
import com.gearsy.gearsy.entity.Cart_Items;
import com.gearsy.gearsy.entity.Users;
import com.gearsy.gearsy.service.AuthService;
import com.gearsy.gearsy.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user/cart")
@ControllerAdvice
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final AuthService userService;
    private final CouponService couponService;
    private final UserCouponRepository userCouponRepo;

    @GetMapping
    public String viewCart(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        String email = principal.getName();
        Users user = userService.getUserByEmail(email);

        // Lấy danh sách sản phẩm trong giỏ
        List<Cart_Items> items = cartService.getCartItems(user);

        // Đếm tổng số lượng sản phẩm
        int itemCount = items.stream()
                .mapToInt(Cart_Items::getQuantity)
                .sum();

        // Tính tổng tiền
        BigDecimal total = items.stream()
                .map(i -> i.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Lấy danh sách coupon khả dụng
        List<User_Coupons> coupons = couponService.getAvailableCoupons(user);

        // Truyền dữ liệu ra view
        model.addAttribute("items", items);
        model.addAttribute("cartCount", itemCount);
        model.addAttribute("title", "Giỏ hàng của bạn");
        model.addAttribute("total", total);
        model.addAttribute("coupons", coupons);
        model.addAttribute("contentTemplate", "/cart/view");

        return "layout";
    }

    @GetMapping("/apply-coupon")
    @ResponseBody
    public Map<String, Object> applyCoupon(@RequestParam Long couponId,
                                           Principal principal) {
        Users user = userService.getUserByEmail(principal.getName());
        BigDecimal total = cartService.calculateTotal(user);

        User_Coupons userCoupon = userCouponRepo.findById(couponId).orElse(null);
        if (userCoupon == null) {
            return Map.of("finalPrice", total);
        }

        Coupons coupon = userCoupon.getCoupon();
        BigDecimal discount = total.multiply(coupon.getDiscountPercent().divide(BigDecimal.valueOf(100)));
        BigDecimal finalPrice = total.subtract(discount);

        return Map.of(
                "finalPrice", finalPrice,
                "discount", discount,
                "couponCode", coupon.getCode()
        );
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
