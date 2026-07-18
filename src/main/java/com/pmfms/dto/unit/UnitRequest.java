package com.pmfms.dto.unit;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class UnitRequest {

    @NotNull(message = "Property id is required")
    private Long propertyId;

    @Min(-5) @Max(300)
    private Integer floorNumber;

    @NotNull @DecimalMin(value = "1.0", message = "Area must be positive")
    private BigDecimal areaSqft;

    @Min(0) @Max(50)
    private Integer bedrooms;

    @Min(0) @Max(50)
    private Integer bathrooms;

    @DecimalMin(value = "0.0")
    private BigDecimal monthlyRent;

    private Set<@NotBlank @Size(max = 80) String> amenities;
}
