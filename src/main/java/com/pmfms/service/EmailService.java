package com.pmfms.service;

/**
 * Notification abstraction (BRD 11.4 - SMS/Email gateway integration point).
 * Default implementation logs to console; swap in an SMTP/SendGrid/SES
 * implementation for production without touching callers.
 */
public interface EmailService {

    void sendPasswordResetEmail(String to, String resetToken);
}
