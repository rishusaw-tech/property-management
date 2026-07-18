package com.pmfms.entity;

import com.pmfms.enums.VendorCategory;
import com.pmfms.enums.VendorStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Vendor / contractor company profile (BRD 9.4) with contract dates and
 * performance rating. Optionally linked to a User (role VENDOR) for portal login.
 */
@Entity
@Table(name = "vendors", indexes = {
        @Index(name = "idx_vendors_status", columnList = "status"),
        @Index(name = "idx_vendors_category", columnList = "category")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false, length = 160)
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VendorCategory category;

    @Column(name = "contact_name", length = 120)
    private String contactName;

    @Column(name = "contact_email", nullable = false, length = 160)
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    /** Performance rating 0.00 - 5.00 (BRD 9.4 SLA scorecards). */
    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VendorStatus status;

    @Column(name = "contract_start")
    private LocalDate contractStart;

    @Column(name = "contract_end")
    private LocalDate contractEnd;

    /** Optional portal login account (role VENDOR). */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate  void onUpdate() { updatedAt = Instant.now(); }
}
