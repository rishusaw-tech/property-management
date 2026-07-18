package com.pmfms.api;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.user.CreateUserRequest;
import com.pmfms.dto.user.UserResponse;
import com.pmfms.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "2. Users", description = "User administration (BRD 10.2.1 - User & Role Management)")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/users")
public interface UserApi {

    @Operation(summary = "Create a user with a role", description = "ADMIN only. Creates managers, owners, tenants or vendor logins.")
    @PostMapping
    ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request);

    @Operation(summary = "Get current user profile")
    @GetMapping("/me")
    ResponseEntity<UserResponse> me();

    @Operation(summary = "Get user by id")
    @GetMapping("/{id}")
    ResponseEntity<UserResponse> getById(@PathVariable Long id);

    @Operation(summary = "List users (paginated)", description = "Optionally filter by role.")
    @GetMapping
    ResponseEntity<PageResponse<UserResponse>> list(
            @Parameter(description = "Filter by role") @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);
}
