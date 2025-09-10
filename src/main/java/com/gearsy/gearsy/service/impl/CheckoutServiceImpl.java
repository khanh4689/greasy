package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.dto.MomoPaymentResponse;
import com.gearsy.gearsy.entity.*;
import com.gearsy.gearsy.repository.*;
import com.gearsy.gearsy.security.HmacUtils;
import com.gearsy.gearsy.service.CartService;
import com.gearsy.gearsy.service.CheckoutService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final OrderRepository orderRepo;
    private final OrderDetailsRepository orderDetailsRepo;
    private final PaymentRepository paymentRepo;
    private final ProductsRepository productRepo;
    private final CartService cartService;

    @Override
    public Orders createOrder(Users user, String shippingAddress) {
        List<Cart_Items> cartItems = cartService.getCartItems(user);
        BigDecimal total = cartItems.stream()
                .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Orders order = new Orders(user, total, LocalDateTime.now(), "PROCESSING",
                shippingAddress, BigDecimal.valueOf(20000), false, LocalDateTime.now(), LocalDateTime.now());
        order = orderRepo.save(order);

        for (Cart_Items item : cartItems) {
            orderDetailsRepo.save(new OrderDetails(null, order, item.getProduct(),
                    item.getQuantity(), item.getProduct().getPrice()));
        }

        return order;
    }

    @Override
    public void checkoutCOD(Users user, String shippingAddress) {
        Orders order = createOrder(user, shippingAddress);
        List<Cart_Items> cartItems = cartService.getCartItems(user);

        for (Cart_Items item : cartItems) {
            Products product = item.getProduct();
            int stock = product.getStock() - item.getQuantity();
            if (stock < 0) throw new IllegalStateException("Không đủ hàng tồn");
            product.setStock(stock);
            product.setUpdatedAt(LocalDateTime.now());
            productRepo.save(product);
        }

        Payments payment = new Payments(null, order, "COD", LocalDateTime.now(), "PENDING");
        paymentRepo.save(payment);

        cartService.clearCart(user);
    }

    @Override
    public String createMomoPayment(Orders order) {
        try {
            String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
            String partnerCode = "MOMOXXXX20230801";
            String accessKey = "your_access_key";
            String secretKey = "your_secret_key";
            String requestId = UUID.randomUUID().toString();
            String orderId = order.getOrderId().toString();
            String amount = String.valueOf(Math.max(order.getTotalAmount().intValue(), 10000));
            String orderInfo = "Thanh toán đơn hàng #" + orderId;
            String redirectUrl = "http://localhost:8080/user/payment/momo-return";
            String ipnUrl = "http://localhost:8080/user/payment/momo-ipn";
            String requestType = "captureWallet";
            String extraData = "";

            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = HmacUtils.hmacSha256Hex(secretKey, rawSignature);

            Map<String, Object> body = new HashMap<>();
            body.put("partnerCode", partnerCode);
            body.put("accessKey", accessKey);
            body.put("requestId", requestId);
            body.put("amount", amount);
            body.put("orderId", orderId);
            body.put("orderInfo", orderInfo);
            body.put("redirectUrl", redirectUrl);
            body.put("ipnUrl", ipnUrl);
            body.put("extraData", extraData);
            body.put("requestType", requestType);
            body.put("signature", signature);
            body.put("lang", "vi");

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<MomoPaymentResponse> response = restTemplate.postForEntity(
                    endpoint, new HttpEntity<>(body), MomoPaymentResponse.class
            );

            return response.getBody().getPayUrl();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể tạo thanh toán MoMo");
        }
    }



    @Override
    public void processMomoReturn(Long orderId) {
        Orders order = orderRepo.findById(orderId).orElseThrow();
        List<OrderDetails> orderDetails = orderDetailsRepo.findByOrder(order);

        for (OrderDetails item : orderDetails) {
            Products product = item.getProduct();
            int newStock = product.getStock() - item.getQuantity();
            if (newStock < 0) throw new IllegalStateException("Không đủ hàng tồn");
            product.setStock(newStock);
            product.setUpdatedAt(LocalDateTime.now());
            productRepo.save(product);
        }

        Payments payment = new Payments(null, order, "MOMO", LocalDateTime.now(), "SUCCESS");
        paymentRepo.save(payment);

        order.setStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepo.save(order);
    }

    @Override
    public String createVnpayPayment(Orders order) {
        try {
            // ✅ Thông tin cấu hình
            String vnp_TmnCode = "J7J0N10H";
            String vnp_HashSecret = "7IAAYGDV24FUCCT9U6KJZ6KON6GCYZHY";
            String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
            String vnp_ReturnUrl = "http://localhost:8080/user/payment/vnpay-return";

            // ✅ Xử lý tổng tiền (làm tròn & đảm bảo >= 10,000)
            BigDecimal totalAmount = order.getTotalAmount();
            if (totalAmount.compareTo(BigDecimal.valueOf(10000)) < 0) {
                totalAmount = BigDecimal.valueOf(10000);
            }
            String amount = totalAmount.multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP)
                    .toString();

            // ✅ Khởi tạo các tham số
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", amount); // đơn vị VND x 100
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", String.valueOf(order.getOrderId()));
            vnp_Params.put("vnp_OrderInfo", "Thanh toán đơn hàng #" + order.getOrderId());
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", "127.0.0.1");
            vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

            // ✅ Sắp xếp thứ tự tham số để ký
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String field : fieldNames) {
                String value = vnp_Params.get(field);
                if (value != null && !value.isEmpty()) {
                    String encoded = URLEncoder.encode(value, StandardCharsets.US_ASCII.toString());
                    hashData.append(field).append('=').append(encoded).append('&');
                    query.append(field).append('=').append(encoded).append('&');
                }
            }

            // ✅ Xóa dấu & cuối cùng
            if (hashData.length() > 0) {
                hashData.setLength(hashData.length() - 1);
                query.setLength(query.length() - 1);
            }

            // ✅ Tạo chữ ký HMAC SHA512
            String secureHash = HmacUtils.hmacSha512Hex(vnp_HashSecret, hashData.toString());
            query.append("&vnp_SecureHash=").append(secureHash);

            // ✅ Trả về URL thanh toán
            return vnp_Url + "?" + query.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể tạo thanh toán VNPay", e);
        }
    }


    @Override
    public void processVnpayReturn(Long orderId) {
        Orders order = orderRepo.findById(orderId).orElseThrow();
        List<OrderDetails> orderDetails = orderDetailsRepo.findByOrder(order);

        for (OrderDetails item : orderDetails) {
            Products product = item.getProduct();
            int newStock = product.getStock() - item.getQuantity();
            if (newStock < 0) throw new IllegalStateException("Không đủ hàng tồn");
            product.setStock(newStock);
            product.setUpdatedAt(LocalDateTime.now());
            productRepo.save(product);
        }

        Payments payment = new Payments(null, order, "VNPAY", LocalDateTime.now(), "SUCCESS");
        paymentRepo.save(payment);

        order.setStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepo.save(order);
    }

    @Override
    public Orders getOrderById(Long orderId) {
        return orderRepo.findById(orderId).orElseThrow();
    }

    @Override
    public List<OrderDetails> getOrderDetails(Orders order) {
        return orderDetailsRepo.findByOrder(order);
    }


}
