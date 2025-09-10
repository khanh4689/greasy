package com.gearsy.gearsy.dto;

import com.gearsy.gearsy.entity.UserRole;
import com.gearsy.gearsy.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminDTO {
    private Long id;
    private String email;
    private String phone;
    private String password;
    private String name;
    private String avatar;
    private String address;
    private UserRole role;
    private UserStatus status;
}