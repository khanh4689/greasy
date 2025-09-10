package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.Orders;
import com.gearsy.gearsy.security.HmacUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnPayService {

    @Value("${VNPAY_VERSION}")
    private String VNPAY_VERSION;

    @Value("${VNPAY_COMMAND}")
    private String VNPAY_COMMAND;

    @Value("${VNPAY_TMNCODE}")
    private String VNP_TMNCODE;

    @Value("${VNPAY_HASHSECRET}")
    private String VNP_HASH_SECRET;

    @Value("${VNPAY_PAYURL}")
    private String VNP_PAY_URL;

    @Value("${VNPAY_RETURNURL}")
    private String VNP_RETURN_URL;

    public String createPaymentUrl(HttpServletRequest request, Orders order) throws UnsupportedEncodingException {
        String orderType = "other";
        long amount = order.getTotalAmount().longValue() * 100L; // VNPay yêu cầu đơn vị là x100

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", VNPAY_VERSION);
        vnpParams.put("vnp_Command", VNPAY_COMMAND);
        vnpParams.put("vnp_TmnCode", VNP_TMNCODE);
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", order.getOrderId().toString());
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang #" + order.getOrderId());
        vnpParams.put("vnp_OrderType", orderType);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", VNP_RETURN_URL);
        vnpParams.put("vnp_IpAddr", request.getRemoteAddr());
        vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String name : fieldNames) {
            String value = vnpParams.get(name);
            if (hashData.length() > 0) hashData.append('&');
            hashData.append(name).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            query.append(name).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
        }

        String vnp_SecureHash = HmacUtils.hmacSha512Hex(VNP_HASH_SECRET, hashData.toString());
        query.append("vnp_SecureHash=").append(vnp_SecureHash);

        return VNP_PAY_URL + "?" + query.toString();
    }
}
