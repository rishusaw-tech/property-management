package com.pmfms.dto.compliance;

import com.pmfms.enums.CertificateType;
import com.pmfms.enums.ComplianceStatus;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class ComplianceRecordResponse {
    private Long id;
    private Long propertyId;
    private String propertyName;
    private CertificateType certificateType;
    private String certificateNumber;
    private String issuedBy;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private ComplianceStatus status;
    private Instant createdAt;
}
