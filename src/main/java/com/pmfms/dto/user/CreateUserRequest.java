package com.pmfms.dto.user;

import com.pmfms.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/** Admin-only: create a staff/tenant/owner/vendor user with an explicit role. */
@Data
public class CreateUserRequest {

    @NotBlank @Size(max = 120)
    private String fullName;

    @NotBlank @Email @Size(max = 160)
    private String email;

    @NotBlank @Size(min = 8, max = 100)
    private String password;

    @Pattern(regexp = "^$|^[+0-9][0-9 \\-]{6,18}$", message = "Invalid phone number")
    private String phone;

    @NotNull(message = "Role is required")
    @Schema(example = "PROPERTY_MANAGER")
    private Role role;
}
