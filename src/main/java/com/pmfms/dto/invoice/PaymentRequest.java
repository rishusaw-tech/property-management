package com.pmfms.dto.invoice;

import com.pmfms.enums.PaymentMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    /** Idempotency key - the same reference is never processed twice (BRD 11.3). */
    @NotBlank @Size(max = 60)
    @Schema(example = "TXN-2026-000123")
    private String reference;

    @NotNull @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull
    private PaymentMode mode;
}
