package com.pmfms.entity;

import com.pmfms.enums.WorkOrderPriority;
import com.pmfms.enums.WorkOrderStatus;
import com.pmfms.enums.WorkOrderType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Work order / maintenance ticket (BRD 9.2, 9.3, 14.1).
 * SLA due timestamp is computed from priority at creation time.
 * May target a Unit and/or a specific Asset within a Property.
 */
@Entity
@Table(name = "work_orders", indexes = {
        @Index(name = "idx_wo_number", columnList = "number", unique = true),
        @Index(name = "idx_wo_property", columnList = "property_id"),
        @Index(name = "idx_wo_status", columnList = "status"),
        @Index(name = "idx_wo_priority", columnList = "priority"),
        @Index(name = "idx_wo_sla_due", columnList = "sla_due_at")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkOrderType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private WorkOrderPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkOrderStatus status;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "raised_by_id", nullable = false)
    private User raisedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_vendor_id")
    private Vendor assignedVendor;

    /** SLA breach detection reference point (BRD 9.3 / 15.1). */
    @Column(name = "sla_due_at", nullable = false)
    private Instant slaDueAt;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate  void onUpdate() { updatedAt = Instant.now(); }
}
