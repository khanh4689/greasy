package com.gearsy.gearsy.controller;

import com.gearsy.gearsy.config.VnPayConfig;
import com.gearsy.gearsy.entity.Orders;
import com.gearsy.gearsy.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/payment")
public class VnPayReturnController {

    private final OrderRepository orderRepository;

    @GetMapping("/vnpay-return")
    public String handleVnPayReturn(HttpServletRequest request, Model model) {
        Map<String, String> vnp_Params = new HashMap<>();
        Map<String, String[]> fields = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : fields.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            if (value.length > 0) {
                vnp_Params.put(key, value[0]);
            }
        }

        String vnp_SecureHash = vnp_Params.remove("vnp_SecureHash");
        vnp_Params.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = vnp_Params.get(fieldName);
            hashData.append(fieldName).append('=').append(fieldValue);
            if (i < fieldNames.size() - 1) {
                hashData.append('&');
            }
        }

        String signCheck = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, hashData.toString());
        String vnp_ResponseCode = vnp_Params.get("vnp_ResponseCode");
        String orderCode = vnp_Params.get("vnp_TxnRef");

        if (signCheck.equals(vnp_SecureHash)) {
            Optional<Orders> optionalOrder = orderRepository.findAll()
                    .stream().filter(o -> o.getOrderId().toString().equals(orderCode)).findFirst();

            if (optionalOrder.isPresent()) {
                Orders order = optionalOrder.get();
                if ("00".equals(vnp_ResponseCode)) {
                    order.setStatus("PAID");
                    model.addAttribute("message", "Thanh toán thành công!");
                } else {
                    order.setStatus("FAILED");
                    model.addAttribute("message", "Thanh toán thất bại!");
                }
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
            } else {
                model.addAttribute("message", "Không tìm thấy đơn hàng!");
            }
        } else {
            model.addAttribute("message", "Xác minh chữ ký thất bại!");
        }

        return "cart/payment-result"; // Thymeleaf view
    }
}