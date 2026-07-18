package com.pmfms.service.impl;

import com.pmfms.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Development implementation - logs the reset link to the console.
 * For production, replace with an SMTP (spring-boot-starter-mail),
 * SendGrid or SES implementation of {@link EmailService}.
 */
@Service
@Slf4j
public class ConsoleEmailService implements EmailService {

    @Value("${app.frontend.reset-password-url}")
    private String resetPasswordUrl;

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        log.info("""
                        
                        ==================== PASSWORD RESET EMAIL (DEV) ====================
                        To      : {}
                        Subject : Reset your PMFMS password
                        Link    : {}?token={}
                        (Token valid for a limited time, single use)
                        ====================================================================
                        """,
                to, resetPasswordUrl, resetToken);
    }
}
