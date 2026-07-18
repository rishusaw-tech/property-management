package com.pmfms.entity;

import com.pmfms.enums.AssetCategory;
import com.pmfms.enums.AssetStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Central asset register entry - HVAC, elevators, generators etc. (BRD 9.1)
 * with make/model/serial/warranty and lifecycle tracking.
 */
@Entity
@Table(name = "assets", indexes = {
        @Index(name = "idx_assets_tag", columnList = "tag", unique = true),
        @Index(name = "idx_assets_property", columnList = "property_id"),
        @Index(name = "idx_assets_status", columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** QR/Barcode tag for field verification (BRD 9.1). */
    @Column(nullable = false, unique = true, length = 40)
    private String tag;

    @Column(nullable = false, length = 120)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssetCategory category;

    @Column(length = 80)
    private String make;

    @Column(length = 80)
    private String model;

    @Column(name = "serial_number", length = 120)
    private String serialNumber;

    @Column(name = "installed_on")
    private LocalDate installedOn;

    @Column(name = "warranty_expiry")
    private LocalDate warrantyExpiry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssetStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate  void onUpdate() { updatedAt = Instant.now(); }
}
