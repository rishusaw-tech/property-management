package com.pmfms.service.impl;

import com.pmfms.dto.auth.ForgotPasswordRequest;
import com.pmfms.dto.auth.ResetPasswordRequest;
import com.pmfms.entity.PasswordResetToken;
import com.pmfms.entity.User;
import com.pmfms.repository.PasswordResetTokenRepository;
import com.pmfms.repository.RefreshTokenRepository;
import com.pmfms.repository.UserRepository;
import com.pmfms.service.EmailService;
import com.pmfms.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.password-reset.expiration-ms}")
    private long resetExpirationMs;

    @Override
    @Transactional
    public void requestReset(ForgotPasswordRequest request) {
        // Deliberately silent when email is unknown - prevents email enumeration
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            resetTokenRepository.deleteByUser(user); // one active reset token per user

            PasswordResetToken token = PasswordResetToken.builder()
                    .token(UUID.randomUUID().toString())
                    .user(user)
                    .expiryDate(Instant.now().plusMillis(resetExpirationMs))
                    .build();
            resetTokenRepository.save(token);

            emailService.sendPasswordResetEmail(user.getEmail(), token.getToken());
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = resetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or already used reset token"));

        if (token.isExpired()) {
            resetTokenRepository.delete(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset token has expired, please request a new one");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetTokenRepository.delete(token);       // single-use
        refreshTokenRepository.deleteByUser(user); // revoke all sessions after password change
    }
}
