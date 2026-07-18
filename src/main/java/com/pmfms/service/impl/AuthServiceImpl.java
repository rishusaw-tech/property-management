package com.pmfms.service.impl;

import com.pmfms.dto.auth.*;
import com.pmfms.dto.user.UserResponse;
import com.pmfms.entity.RefreshToken;
import com.pmfms.entity.User;
import com.pmfms.enums.Role;
import com.pmfms.mapper.EntityMapper;
import com.pmfms.repository.RefreshTokenRepository;
import com.pmfms.repository.UserRepository;
import com.pmfms.security.JwtService;
import com.pmfms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EntityMapper mapper;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Override
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.TENANT) // public signups are tenants; staff users are created by ADMIN
                .active(true)
                .build();
        user = userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (stored.isExpired()) {
            refreshTokenRepository.delete(stored);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired, please login again");
        }

        User user = stored.getUser();

        // Rotate: revoke old token, issue a new one for this device session
        refreshTokenRepository.delete(stored);
        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        // Revoke only this device's session; unknown tokens are ignored silently
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);
    }

    @Override
    @Transactional
    public void logoutAll(String email) {
        User user = findUser(email);
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    @Transactional
    public void closeAccount(String email, DeleteAccountRequest request) {
        User user = findUser(email);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password");
        }

        refreshTokenRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    // ===== helpers =====

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = createRefreshToken(user);
        UserResponse userDto = mapper.map(user, UserResponse.class);
        return new AuthResponse(accessToken, refreshToken, "Bearer", jwtService.getExpirationMs(), userDto);
    }

    private String createRefreshToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID() + "-" + UUID.randomUUID())
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .build();
        return refreshTokenRepository.save(token).getToken();
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
