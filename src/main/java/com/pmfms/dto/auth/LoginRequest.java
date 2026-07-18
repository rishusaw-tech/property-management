package com.pmfms.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Login is available with email + password only. */
@Data
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(example = "admin@pmfms.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(example = "Admin@123")
    private String password;
}
