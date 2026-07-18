package com.pmfms.dto.vendor;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/** SLA scorecard rating update (BRD 9.4). */
@Data
public class VendorRatingRequest {

    @NotNull
    @DecimalMin(value = "0.0") @DecimalMax(value = "5.0")
    private BigDecimal rating;
}
