package com.pmfms.dto.lease;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Renewal with rent revision (BRD 8.5). */
@Data
public class LeaseRenewRequest {

    @NotNull @Future(message = "New end date must be in the future")
    private LocalDate newEndDate;

    @NotNull @DecimalMin(value = "0.01", message = "Revised rent must be positive")
    private BigDecimal revisedMonthlyRent;
}
