package com.pmfms.dto.user;

import com.pmfms.enums.Role;
import lombok.Data;

import java.time.Instant;

@Data
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private Boolean active;
    private Instant createdAt;
}
