package com.gearsy.gearsy.service;

import com.gearsy.gearsy.dto.AuthRequest;
import com.gearsy.gearsy.dto.AuthResponse;
import com.gearsy.gearsy.dto.RegisterRequest;
import com.gearsy.gearsy.entity.UserRole;
import com.gearsy.gearsy.entity.UserStatus;
import com.gearsy.gearsy.entity.Users;
import com.gearsy.gearsy.repository.UsersRepository;
import com.gearsy.gearsy.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest request) {
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Users newUser = new Users();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setName(request.getName());
        newUser.setRole(UserRole.USER);
        newUser.setStatus(UserStatus.Inactive); // chờ xác thực email
        newUser.setCreatedAt(LocalDateTime.now());

        usersRepository.save(newUser);

        // Tạo token xác thực email
        String verificationToken = jwtUtils.generateToken(newUser.getEmail());

        // Gửi email xác thực
        String verifyLink = "http://localhost:8080/auth/verify?token=" + verificationToken;
        emailService.sendEmail(
                newUser.getEmail(),
                "GearsY - Verify your email",
                "Click the link to verify your account: " + verifyLink
        );

        return "Registration successful. Check your email for verification.";
    }

    public String verifyEmail(String token) {
        if (!jwtUtils.isTokenValid(token)) {
            throw new RuntimeException("Invalid or expired token.");
        }

        String email = jwtUtils.extractUsername(token);
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.Active);
        usersRepository.save(user);

        return "Email verified successfully!";
    }

    public AuthResponse login(AuthRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword())
        );

        Users users = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new RuntimeException("Email not found"));
        String token = jwtUtils.generateToken(users.getEmail());
        return new AuthResponse(token);
    }

    public String sendResetPasswordEmail(String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String token = jwtUtils.generateToken(email); // có thể rút ngắn thời gian sống
        String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;

        emailService.sendEmail(
                email,
                "Password Reset Request",
                "Click this link to reset your password: " + resetLink
        );

        return "Reset link sent to email.";
    }

    public String resetPassword(String token, String newPassword) {
        if (!jwtUtils.isTokenValid(token)) {
            throw new RuntimeException("Invalid or expired token.");
        }

        String email = jwtUtils.extractUsername(token);
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(user);

        return "Password reset successfully.";
    }


    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
