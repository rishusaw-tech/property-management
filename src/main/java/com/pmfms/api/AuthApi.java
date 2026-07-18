package com.pmfms.api;

import com.pmfms.dto.auth.*;
import com.pmfms.dto.common.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1. Authentication", description = "Signup, login (email + password only), token refresh, logout and account closure")
@RequestMapping("/api/v1/auth")
public interface AuthApi {

    @Operation(summary = "Sign up", description = "Creates the user (role TENANT) and returns access + refresh tokens (auto-login).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    @PostMapping("/signup")
    ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request);

    @Operation(summary = "Login", description = "Login with email + password only. Each login creates a new device session.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request);

    @Operation(summary = "Refresh session", description = "Exchange a valid refresh token for a new access token. Refresh token is rotated.")
    @PostMapping("/refresh")
    ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request);

    @Operation(summary = "Logout (this device)", description = "Revokes the given refresh token only. Other device sessions remain active.")
    @PostMapping("/logout")
    ResponseEntity<ApiMessage> logout(@Valid @RequestBody LogoutRequest request);

    @Operation(summary = "Logout from all devices", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/logout-all")
    ResponseEntity<ApiMessage> logoutAll();

    @Operation(summary = "Forgot password", description = "Sends a password reset link to the email if an account exists. Always returns 200 (no email enumeration).")
    @PostMapping("/forgot-password")
    ResponseEntity<ApiMessage> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request);

    @Operation(summary = "Reset password", description = "Sets a new password using the reset token. All sessions are revoked afterwards.")
    @PostMapping("/reset-password")
    ResponseEntity<ApiMessage> resetPassword(@Valid @RequestBody ResetPasswordRequest request);

    @Operation(summary = "Close my account", description = "Permanently deletes the account. Requires password confirmation.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/me")
    ResponseEntity<ApiMessage> closeAccount(@Valid @RequestBody DeleteAccountRequest request);
}
