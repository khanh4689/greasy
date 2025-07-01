package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.entity.OrderDetails;
import com.gearsy.gearsy.entity.Orders;
import com.gearsy.gearsy.entity.Payments;
import com.gearsy.gearsy.entity.Products;
import com.gearsy.gearsy.repository.OrderDetailsRepository;
import com.gearsy.gearsy.repository.OrderRepository;
import com.gearsy.gearsy.repository.PaymentRepository;
import com.gearsy.gearsy.repository.ProductsRepository;
import com.gearsy.gearsy.service.OrderService;
import com.gearsy.gearsy.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository ordersRepo;
    private final ProductsRepository productsRepository;
    private final PaymentRepository paymentsRepo;
    private final OrderDetailsRepository orderDetailsRepository;

    @Override
    public String processPayment(Long orderId, String method) {
        Orders order = ordersRepo.findById(orderId).orElseThrow();

        // ✅ Tạo payment
        Payments payment = new Payments();
        payment.setOrder(order);
        payment.setPaymentMethod(method);
        payment.setPaymentStatus("SUCCESS");
        payment.setPaymentDate(LocalDateTime.now());
        paymentsRepo.save(payment);

        // ✅ Trừ tồn kho theo từng sản phẩm
        List<OrderDetails> orderDetails = orderDetailsRepository.findByOrder(order);
        for (OrderDetails item : orderDetails) {
            Products product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Không đủ hàng cho sản phẩm " + product.getName());
            }
            product.setStock(product.getStock() - item.getQuantity());
            productsRepository.save(product);
        }

        // ✅ Cập nhật trạng thái đơn hàng
        order.setStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
        ordersRepo.save(order);

        return "Payment successful with method: " + method;
    }
}

