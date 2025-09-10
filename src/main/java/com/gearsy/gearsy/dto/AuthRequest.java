package com.gearsy.gearsy.dto;

import lombok.Data;

@Data
public class AuthRequest {
    String email;
    String password;
}
