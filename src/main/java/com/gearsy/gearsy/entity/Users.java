package com.gearsy.gearsy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "Users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_phone", columnList = "phone")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    // Cho phép null/blank; nếu có giá trị thì phải là email hợp lệ và <= 150 ký tự
    @Email(message = "Email không hợp lệ")
    @Size(max = 150, message = "Email tối đa 150 ký tự")
    @Column(nullable = true, unique = true) // Không trùng (DB đảm bảo), có thể null
    private String email;

    // Cho phép null/blank; nếu có thì phải đúng định dạng số VN/E.164 phổ biến và <= 20 ký tự
    // Ví dụ chấp nhận: 090xxxxxxx, 09xxxxxxxxx, +849xxxxxxxx
    @Pattern(
            regexp = "^(|\\+?84\\d{9,10}|0\\d{9,10})$",
            message = "Số điện thoại không hợp lệ"
    )
    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    @Column(nullable = true, unique = true) // Nếu muốn cho phép trùng phone thì bỏ unique=true
    private String phone;

    // Password: cho phép null/blank ở entity; nếu form tạo tài khoản cần bắt buộc,
    // hãy enforce ở DTO/Controller (Validation Groups)
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6–100 ký tự")
    @Column(nullable = true) // <-- đổi thành true để cho phép để trống ở entity
    private String password;

    // Tên: optional; nếu có thì giới hạn độ dài và ký tự cơ bản (chữ, số, khoảng trắng, dấu tiếng Việt, một số ký tự tên thường gặp)
    @Size(max = 100, message = "Tên tối đa 100 ký tự")
    @Pattern(
            regexp = "^[\\p{L}0-9 .,'’-]*$",
            message = "Tên chứa ký tự không hợp lệ"
    )
    private String name;

    // Avatar path/URL: optional; nếu có thì giới hạn độ dài và ký tự an toàn đường dẫn
    @Size(max = 255, message = "Avatar tối đa 255 ký tự")
    @Pattern(
            regexp = "^[A-Za-z0-9_./:\\-]*$",
            message = "Đường dẫn avatar không hợp lệ"
    )
    private String avatar;

    // Địa chỉ: optional; nếu có thì chỉ giới hạn độ dài
    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.Active;

    private LocalDateTime createdAt;
    @Column(nullable = false)
    private boolean phoneVerified = false;
    @PrePersist
    public void prePersist() {
        normalizeBlanksToNull();
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        normalizeBlanksToNull();
    }

    /** Chuyển chuỗi rỗng/only-space thành null để không kích hoạt validate sai & không lưu rác */
    private void normalizeBlanksToNull() {
        this.email   = normalize(this.email);
        this.phone   = normalize(this.phone);
        this.password= normalize(this.password);
        this.name    = normalize(this.name);
        this.avatar  = normalize(this.avatar);
        this.address = normalize(this.address);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
