package com.gearsy.gearsy.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HmacUtils {

    /**
     * Tạo chữ ký HMAC SHA256 dạng hex (cho MoMo)
     * @param key   Secret key (MoMo)
     * @param data  Dữ liệu cần ký
     * @return      Chữ ký dạng hex
     */
    public static String hmacSha256Hex(String key, String data) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKey);
            byte[] bytes = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Convert to hex
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo HMAC SHA256", e);
        }
    }

    /**
     * Tạo chữ ký HMAC SHA256 dạng Base64 (nếu MoMo yêu cầu)
     * @param key   Secret key
     * @param data  Dữ liệu cần ký
     * @return      Chữ ký dạng Base64
     */
    public static String hmacSha256Base64(String key, String data) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKey);
            byte[] bytes = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo HMAC SHA256 Base64", e);
        }
    }

    /**
     * Tạo chữ ký HMAC SHA512 dạng hex (cho VNPay)
     * @param key   Secret key (VNPay)
     * @param data  Dữ liệu cần ký
     * @return      Chữ ký dạng hex
     */
    public static String hmacSha512Hex(String key, String data) {
        try {
            Mac hmacSha512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmacSha512.init(secretKey);
            byte[] bytes = hmacSha512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Convert to hex
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo HMAC SHA512", e);
        }
    }

}
