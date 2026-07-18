package com.pmfms.dto.invoice;

import com.pmfms.enums.PaymentMode;
import com.pmfms.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class PaymentResponse {
    private Long id;
    private String reference;
    private Long invoiceId;
    private String invoiceInvoiceNumber;
    private BigDecimal amount;
    private PaymentMode mode;
    private PaymentStatus status;
    private Instant paidAt;
}
