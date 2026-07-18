package com.pmfms.entity;

import com.pmfms.enums.PropertyStatus;
import com.pmfms.enums.PropertyType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A managed property (building / complex). BRD 8.1 & 11.2.
 * Owner is a User with role OWNER.
 */
@Entity
@Table(name = "properties", indexes = {
        @Index(name = "idx_properties_code", columnList = "code", unique = true),
        @Index(name = "idx_properties_status", columnList = "status"),
        @Index(name = "idx_properties_city", columnList = "city")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Auto-generated unique Property ID (BRD 8.1). */
    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "address_line", nullable = false, length = 255)
    private String addressLine;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(length = 80)
    private String state;

    @Column(nullable = false, length = 80)
    private String country;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /** GPS location (BRD 8.1). */
    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PropertyType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PropertyStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate  void onUpdate() { updatedAt = Instant.now(); }
}
