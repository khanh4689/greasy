package com.gearsy.gearsy.controller;

import com.gearsy.gearsy.dto.AuthRequest;
import com.gearsy.gearsy.dto.RegisterRequest;
import com.gearsy.gearsy.dto.ResetPasswordRequest;
import com.gearsy.gearsy.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("authRequest", new AuthRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute AuthRequest authRequest, Model model) {
        try {
            authService.login(authRequest);
            return "redirect:/products/list"; // Trang chủ sau khi đăng nhập
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute RegisterRequest request, Model model) {
        try {
            String result = authService.register(request);
            model.addAttribute("message", result);
            return "auth/verify-info"; // Thông báo đã gửi email xác nhận
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token, Model model) {
        String result = authService.verifyEmail(token);
        model.addAttribute("message", result);
        return "auth/verify-result";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String sendResetEmail(@RequestParam String email, Model model) {
        try {
            String result = authService.sendResetPasswordEmail(email);
            model.addAttribute("message", result);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model model) {
        ResetPasswordRequest resetRequest = new ResetPasswordRequest();
        resetRequest.setToken(token);
        model.addAttribute("resetPasswordRequest", resetRequest);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@ModelAttribute ResetPasswordRequest request, Model model) {
        try {
            String result = authService.resetPassword(request.getToken(), request.getNewPassword());
            model.addAttribute("message", result);
            return "auth/reset-success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/reset-password";
        }
    }
}
