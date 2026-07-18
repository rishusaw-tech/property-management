package com.pmfms.controller;

import com.pmfms.api.UserApi;
import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.user.CreateUserRequest;
import com.pmfms.dto.user.UserResponse;
import com.pmfms.enums.Role;
import com.pmfms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    @PreAuthorize("hasRole('ADMIN')") // BRD 13: only Admin manages users & roles
    public ResponseEntity<UserResponse> create(CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @Override
    public ResponseEntity<UserResponse> me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getByEmail(email));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER','FACILITY_MANAGER')")
    public ResponseEntity<UserResponse> getById(Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER','FACILITY_MANAGER')")
    public ResponseEntity<PageResponse<UserResponse>> list(Role role, int page, int size) {
        return ResponseEntity.ok(userService.list(role, page, size));
    }
}
