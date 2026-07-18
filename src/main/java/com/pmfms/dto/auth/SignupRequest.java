package com.pmfms.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 120)
    @Schema(example = "John Doe")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 160)
    @Schema(example = "john@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be 8-100 characters")
    @Schema(example = "Secret@123")
    private String password;

    @Pattern(regexp = "^$|^[+0-9][0-9 \\-]{6,18}$", message = "Invalid phone number")
    @Schema(example = "+911234567890")
    private String phone;
}
