package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.Products;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("khanhtnts00424@fpt.edu.vn"); // đảm bảo trùng với cấu hình
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
            System.out.println("✅ Email sent to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email to " + to + ": " + e.getMessage());
        }
    }

    public void sendBulkEmail(List<String> toList, String subject, String text) {
        for (String to : toList) {
            sendEmail(to, subject, text);
        }
    }


    public String buildProductAdContent(Products product) {
        return String.format(
                "🎉 New Product Alert! 🎉\n\n" +
                        "Name: %s\n" +
                        "Price: %.0f VND\n" +
                        "Category: %s\n\n" +
                        "Check it out now on our website!",
                product.getName(),
                product.getPrice(),
                product.getCategory().getName()
        );
    }
}
