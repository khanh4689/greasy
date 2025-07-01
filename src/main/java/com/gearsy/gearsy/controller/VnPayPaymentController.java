package com.gearsy.gearsy.controller;

import com.gearsy.gearsy.config.VnPayConfig;
import com.gearsy.gearsy.entity.*;
import com.gearsy.gearsy.service.OrderService;
import com.gearsy.gearsy.repository.CartItemRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/user/payment/vnpay")
@RequiredArgsConstructor
public class VnPayPaymentController {

    private final OrderService orderService;
    private final CartItemRepository cartItemRepository;

    @PostMapping
    public String payWithVnPay(
            @RequestParam String shippingAddress,
            @RequestParam(required = false) String bankCode,
            @RequestParam(required = false) String language,
            HttpServletRequest request
    ) {
        try {
            // 🔐 Giả lập lấy user đang đăng nhập
            Users user = (Users) request.getSession().getAttribute("loggedInUser");
            if (user == null) return "redirect:/login";

            // 🔄 Lấy giỏ hàng người dùng
            List<Cart_Items> cartItems = cartItemRepository.findByCartUser(user);
            if (cartItems.isEmpty()) return "redirect:/cart?empty";

            // 💵 Tính tổng tiền
            BigDecimal totalAmount = cartItems.stream()
                    .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 1️⃣ Tạo đơn hàng
            Orders order = orderService.createOrderFromCart(user, cartItems, shippingAddress, totalAmount);
            String vnp_TxnRef = order.getOrderId().toString();

            // 2️⃣ Chuẩn bị dữ liệu thanh toán
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            long vnp_Amount = totalAmount.multiply(BigDecimal.valueOf(100)).longValue();
            String vnp_IpAddr = VnPayConfig.getIpAddress(request);

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", VnPayConfig.vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(vnp_Amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            if (bankCode != null && !bankCode.isEmpty()) {
                vnp_Params.put("vnp_BankCode", bankCode);
            }
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + vnp_TxnRef);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", (language != null) ? language : "vn");
            vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            // ⏰ Ngày tạo & hết hạn
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
            cld.add(Calendar.MINUTE, 15);
            vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

            // 🔐 Hash & tạo URL thanh toán
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);
                String fieldValue = vnp_Params.get(fieldName);
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (i < fieldNames.size() - 1) {
                    hashData.append('&');
                    query.append('&');
                }
            }

            String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, hashData.toString());
            String paymentUrl = VnPayConfig.vnp_PayUrl + "?" + query + "&vnp_SecureHash=" + vnp_SecureHash;

            return "redirect:" + paymentUrl;

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/payment-error";
        }
    }
}
