package com.pmfms.dto.invoice;

import com.pmfms.enums.InvoiceStatus;
import com.pmfms.enums.InvoiceType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Long leaseId;
    private String leaseCode;
    private InvoiceType type;
    private BigDecimal amount;
    private BigDecimal amountPaid;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private Instant createdAt;
}
