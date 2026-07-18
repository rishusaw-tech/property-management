package com.pmfms.entity;

import com.pmfms.enums.CertificateType;
import com.pmfms.enums.ComplianceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Statutory compliance certificate for a property with expiry tracking
 * (BRD 9.6 - fire safety, lift license, environmental clearance...).
 */
@Entity
@Table(name = "compliance_records", indexes = {
        @Index(name = "idx_compliance_property", columnList = "property_id"),
        @Index(name = "idx_compliance_expiry", columnList = "expiry_date"),
        @Index(name = "idx_compliance_status", columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ComplianceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type", nullable = false, length = 40)
    private CertificateType certificateType;

    @Column(name = "certificate_number", nullable = false, length = 80)
    private String certificateNumber;

    @Column(name = "issued_by", length = 120)
    private String issuedBy;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ComplianceStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist void onCreate() { createdAt = Instant.now(); }
}
