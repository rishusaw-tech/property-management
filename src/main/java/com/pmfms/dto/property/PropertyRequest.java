package com.pmfms.dto.property;

import com.pmfms.enums.PropertyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertyRequest {

    @NotBlank @Size(max = 160)
    @Schema(example = "Sunrise Towers")
    private String name;

    @NotBlank @Size(max = 255)
    private String addressLine;

    @NotBlank @Size(max = 80)
    private String city;

    @Size(max = 80)
    private String state;

    @NotBlank @Size(max = 80)
    private String country;

    @Size(max = 20)
    private String postalCode;

    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    private BigDecimal longitude;

    @NotNull
    private PropertyType type;

    @NotNull(message = "Owner user id is required")
    private Long ownerId;
}
