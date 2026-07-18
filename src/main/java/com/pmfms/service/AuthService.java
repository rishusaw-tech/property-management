package com.pmfms.service;

import com.pmfms.dto.auth.*;

public interface AuthService {

    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshRequest request);

    void logout(LogoutRequest request);

    void logoutAll(String email);

    void closeAccount(String email, DeleteAccountRequest request);
}
