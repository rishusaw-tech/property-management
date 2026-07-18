package com.pmfms.service;

import com.pmfms.dto.auth.ForgotPasswordRequest;
import com.pmfms.dto.auth.ResetPasswordRequest;

public interface PasswordResetService {

    /** Always succeeds silently - no email enumeration. */
    void requestReset(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
