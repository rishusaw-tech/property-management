package com.pmfms.entity;

import com.pmfms.enums.BillingCycle;
import com.pmfms.enums.LeaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * A tenancy agreement binding a Tenant (User) to a Unit. BRD 8.2/8.3 & 14.2.
 */
@Entity
@Table(name = "leases", indexes = {
        @Index(name = "idx_leases_code", columnList = "code", unique = true),
        @Index(name = "idx_leases_unit", columnList = "unit_id"),
        @Index(name = "idx_leases_tenant", columnList = "tenant_id"),
        @Index(name = "idx_leases_status", columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Lease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "monthly_rent", nullable = false, precision = 14, scale = 2)
    private BigDecimal monthlyRent;

    @Column(name = "security_deposit", nullable = false, precision = 14, scale = 2)
    private BigDecimal securityDeposit;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false, length = 20)
    private BillingCycle billingCycle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LeaseStatus status;

    /** Notice period validation for non-renewal (BRD 8.5). */
    @Column(name = "notice_period_days", nullable = false)
    private Integer noticePeriodDays;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate  void onUpdate() { updatedAt = Instant.now(); }
}
