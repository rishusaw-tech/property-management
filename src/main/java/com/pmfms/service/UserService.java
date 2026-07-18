package com.pmfms.service;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.user.CreateUserRequest;
import com.pmfms.dto.user.UserResponse;
import com.pmfms.enums.Role;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    UserResponse getByEmail(String email);

    UserResponse getById(Long id);

    PageResponse<UserResponse> list(Role role, int page, int size);
}
