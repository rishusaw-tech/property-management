package com.pmfms.entity;

import com.pmfms.enums.PaymentMode;
import com.pmfms.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/** A payment recorded against an Invoice (BRD 8.4 - multiple payment modes). */
@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_reference", columnList = "reference", unique = true),
        @Index(name = "idx_payments_invoice", columnList = "invoice_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Idempotency/reconciliation reference (BRD 11.3 idempotent financial APIs). */
    @Column(nullable = false, unique = true, length = 60)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMode mode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "paid_at", nullable = false)
    private Instant paidAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist void onCreate() { createdAt = Instant.now(); }
}
