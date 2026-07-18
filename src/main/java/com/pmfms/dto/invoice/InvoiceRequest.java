package com.pmfms.dto.invoice;

import com.pmfms.enums.InvoiceType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceRequest {

    @NotNull(message = "Lease id is required")
    private Long leaseId;

    @NotNull
    private InvoiceType type;

    @NotNull @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;
}
