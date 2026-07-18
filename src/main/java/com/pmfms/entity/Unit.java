package com.pmfms.entity;

import com.pmfms.enums.UnitStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A rentable unit inside a Property. BRD 8.1 unit-level details.
 * Amenities are normalized into a separate collection table (1NF/2NF).
 */
@Entity
@Table(name = "units", indexes = {
        @Index(name = "idx_units_code", columnList = "code", unique = true),
        @Index(name = "idx_units_property", columnList = "property_id"),
        @Index(name = "idx_units_status", columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Auto-generated unique Unit ID (BRD 8.1). */
    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(name = "area_sqft", nullable = false, precision = 10, scale = 2)
    private BigDecimal areaSqft;

    private Integer bedrooms;

    private Integer bathrooms;

    @Column(name = "monthly_rent", precision = 14, scale = 2)
    private BigDecimal monthlyRent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UnitStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "unit_amenities", joinColumns = @JoinColumn(name = "unit_id"))
    @Column(name = "amenity", nullable = false, length = 80)
    @Builder.Default
    private Set<String> amenities = new LinkedHashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate  void onUpdate() { updatedAt = Instant.now(); }
}
