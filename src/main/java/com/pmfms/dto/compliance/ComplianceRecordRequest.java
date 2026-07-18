package com.pmfms.dto.compliance;

import com.pmfms.enums.CertificateType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ComplianceRecordRequest {

    @NotNull(message = "Property id is required")
    private Long propertyId;

    @NotNull
    private CertificateType certificateType;

    @NotBlank @Size(max = 80)
    private String certificateNumber;

    @Size(max = 120)
    private String issuedBy;

    @NotNull @PastOrPresent(message = "Issue date cannot be in the future")
    private LocalDate issueDate;

    @NotNull @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;
}
