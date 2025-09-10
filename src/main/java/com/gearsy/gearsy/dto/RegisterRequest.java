package com.gearsy.gearsy.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
@Data
public class RegisterRequest {
    @NotBlank(message = "{register.name.required}")
    private String name;

    @Email(message = "{register.email.invalid}")
    private String email;

    @NotBlank(message = "{register.password.required}")
    @Size(min = 6, message = "{register.password.size}")
    private String password;

    @NotBlank(message = "{register.confirmPassword.required}")
    private String confirmPassword;

}
