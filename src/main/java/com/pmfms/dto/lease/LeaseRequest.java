package com.pmfms.dto.lease;

import com.pmfms.enums.BillingCycle;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LeaseRequest {

    @NotNull(message = "Unit id is required")
    private Long unitId;

    @NotNull(message = "Tenant user id is required")
    private Long tenantId;

    @NotNull @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @NotNull @DecimalMin(value = "0.01", message = "Rent must be positive")
    private BigDecimal monthlyRent;

    @NotNull @DecimalMin(value = "0.0")
    private BigDecimal securityDeposit;

    @NotNull
    private BillingCycle billingCycle;

    @NotNull @Min(0) @Max(365)
    private Integer noticePeriodDays;
}
