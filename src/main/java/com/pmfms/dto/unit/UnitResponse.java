package com.pmfms.dto.unit;

import com.pmfms.enums.UnitStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Data
public class UnitResponse {
    private Long id;
    private String code;
    private Long propertyId;
    private String propertyName;
    private Integer floorNumber;
    private BigDecimal areaSqft;
    private Integer bedrooms;
    private Integer bathrooms;
    private BigDecimal monthlyRent;
    private UnitStatus status;
    private Set<String> amenities;
    private Instant createdAt;
    private Instant updatedAt;
}
