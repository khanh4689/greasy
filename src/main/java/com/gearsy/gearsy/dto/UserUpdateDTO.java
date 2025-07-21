package com.gearsy.gearsy.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserUpdateDTO {
    private String name;
    private String phone;
    private String address;
    private MultipartFile avatarFile;
}
