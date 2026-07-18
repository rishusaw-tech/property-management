package com.pmfms.dto.property;

import com.pmfms.enums.PropertyStatus;
import com.pmfms.enums.PropertyType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class PropertyResponse {
    private Long id;
    private String code;
    private String name;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private PropertyType type;
    private PropertyStatus status;
    private Long ownerId;
    private String ownerFullName;
    private Instant createdAt;
    private Instant updatedAt;
}
