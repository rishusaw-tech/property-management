package com.pmfms.controller;

import com.pmfms.api.AuthApi;
import com.pmfms.dto.auth.*;
import com.pmfms.dto.common.ApiMessage;
import com.pmfms.service.AuthService;
import com.pmfms.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @Override
    public ResponseEntity<AuthResponse> signup(SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Override
    public ResponseEntity<AuthResponse> refresh(RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @Override
    public ResponseEntity<ApiMessage> logout(LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(new ApiMessage("Logged out from this device"));
    }

    @Override
    public ResponseEntity<ApiMessage> logoutAll() {
        authService.logoutAll(currentEmail());
        return ResponseEntity.ok(new ApiMessage("Logged out from all devices"));
    }

    @Override
    public ResponseEntity<ApiMessage> forgotPassword(ForgotPasswordRequest request) {
        passwordResetService.requestReset(request);
        return ResponseEntity.ok(new ApiMessage(
                "If an account exists for that email, a password reset link has been sent"));
    }

    @Override
    public ResponseEntity<ApiMessage> resetPassword(ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(new ApiMessage("Password has been reset, please login with your new password"));
    }

    @Override
    public ResponseEntity<ApiMessage> closeAccount(DeleteAccountRequest request) {
        authService.closeAccount(currentEmail(), request);
        return ResponseEntity.ok(new ApiMessage("Your account has been permanently deleted"));
    }

    private String currentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
