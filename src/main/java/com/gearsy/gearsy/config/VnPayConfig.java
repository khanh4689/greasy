package com.gearsy.gearsy.config;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class VnPayConfig {

    public static final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String vnp_ReturnUrl = "http://localhost:8080/user/payment/vnpay-return";
    public static final String vnp_TmnCode = "J7J0N10H"; // 🔁 Thay bằng TMN Code thật
    public static final String secretKey = "7IAAYGDV24FUCCT9U6KJZ6KON6GCYZHY"; // 🔁 Thay bằng secret key thật

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) throw new NullPointerException();
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        try {
            String ip = request.getHeader("X-FORWARDED-FOR");
            return (ip != null) ? ip : request.getRemoteAddr();
        } catch (Exception e) {
            return "Invalid IP: " + e.getMessage();
        }
    }

    public static String getRandomNumber(int len) {
        String digits = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        Random rnd = new Random();
        for (int i = 0; i < len; i++) {
            sb.append(digits.charAt(rnd.nextInt(digits.length())));
        }
        return sb.toString();
    }
}
