package com.pmfms.service.impl;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.user.CreateUserRequest;
import com.pmfms.dto.user.UserResponse;
import com.pmfms.entity.User;
import com.pmfms.enums.Role;
import com.pmfms.mapper.EntityMapper;
import com.pmfms.repository.UserRepository;
import com.pmfms.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .active(true)
                .build();

        return mapper.map(userRepository.save(user), UserResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return mapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        return mapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> list(Role role, int page, int size) {
        Page<User> result = userRepository.findAllByOptionalRole(role,
                PageRequest.of(page, size, Sort.by("id").descending()));
        return PageResponse.of(result, mapper.mapPage(result, UserResponse.class));
    }
}
