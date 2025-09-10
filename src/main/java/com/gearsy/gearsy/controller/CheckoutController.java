package com.gearsy.gearsy.controller;

import com.gearsy.gearsy.entity.*;
import com.gearsy.gearsy.repository.UserCouponRepository;
import com.gearsy.gearsy.service.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final AuthService userService;
    private final InvoicePdfService invoicePdfService;
    private final CartService cartService;
    private final CouponService couponService;
    private final UserCouponRepository userCouponRepo;

    // ✅ Thanh toán Momo
    @GetMapping("/payment/momo-return")
    public String momoReturn(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        String resultCode = params.get("resultCode");
        String orderId = params.get("orderId");

        if ("0".equals(resultCode)) {
            checkoutService.processMomoReturn(Long.valueOf(orderId));
            return "redirect:/user/invoice/" + orderId;
        } else {
            redirectAttributes.addFlashAttribute("error", "Thanh toán MoMo thất bại!");
            return "redirect:/user/cart";
        }
    }

    // ✅ Thanh toán VNPAY
    @GetMapping("/payment/vnpay-return")
    public String vnpayReturn(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        String responseCode = params.get("vnp_ResponseCode");
        String orderId = params.get("vnp_TxnRef");

        if ("00".equals(responseCode)) {
            checkoutService.processVnpayReturn(Long.valueOf(orderId));
            return "redirect:/user/invoice/" + orderId;
        } else {
            redirectAttributes.addFlashAttribute("error", "Thanh toán VNPay thất bại!");
            return "redirect:/user/cart";
        }
    }

    // ✅ Trang hóa đơn sau thanh toán
    @GetMapping("/invoice/{orderId}")
    public String showInvoice(@PathVariable Long orderId, Model model) {
        Orders order = checkoutService.getOrderById(orderId);
        List<OrderDetails> orderDetails = checkoutService.getOrderDetails(order);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderDetails item : orderDetails) {
            total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("total", total);
        model.addAttribute("contentTemplate", "checkout/invoice");
        return "layout";
    }

    // ✅ Chức năng tải file PDF hóa đơn
    @GetMapping("/invoice/pdf/{orderId}")
    public ResponseEntity<byte[]> exportInvoicePdf(@PathVariable Long orderId) {
        Orders order = checkoutService.getOrderById(orderId);
        List<OrderDetails> orderDetails = checkoutService.getOrderDetails(order);

        ByteArrayInputStream pdfStream = invoicePdfService.generateInvoicePdf(order, orderDetails);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + orderId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }

    // ✅ Xử lý khi người dùng đặt hàng
    @PostMapping("/checkout")
    public String handleCheckout(@RequestParam String shippingAddress,
                                 @RequestParam String paymentMethod,
                                 @RequestParam(required = false) Long couponId,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        Users user = userService.getUserByEmail(principal.getName());

        // Tính tổng tiền gốc
        BigDecimal total = cartService.calculateTotal(user);

        Coupons appliedCoupon = null;
        if (couponId != null) {
            User_Coupons userCoupon = userCouponRepo.findById(couponId).orElse(null);
            if (userCoupon != null && userCoupon.getUsedAt() == null) {
                appliedCoupon = userCoupon.getCoupon();
                total = couponService.applyCoupon(total, appliedCoupon);
            }
        }

        // tạo order với total mới và coupon (nếu có)
        Orders order = checkoutService.createOrder(user, shippingAddress);

        // nếu có coupon thì update lại userCoupon
        if (appliedCoupon != null) {
            User_Coupons userCoupon = userCouponRepo.findById(couponId).orElse(null);
            if (userCoupon != null) {
                userCoupon.setUsedAt(LocalDateTime.now());
                userCoupon.setOrder(order);
                userCouponRepo.save(userCoupon);
            }
        }

        switch (paymentMethod) {
            case "COD":
                checkoutService.checkoutCOD(user, shippingAddress);
                return "redirect:/user/cart?success";
            case "MOMO":
                String momoUrl = checkoutService.createMomoPayment(order);
                return "redirect:" + momoUrl;
            case "VNPAY":
                String vnpayUrl = checkoutService.createVnpayPayment(order);
                return "redirect:" + vnpayUrl;
            default:
                return "redirect:/user/cart?error";
        }
    }

}
