package com.pmfms.dto.asset;

import com.pmfms.enums.AssetCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AssetRequest {

    @NotBlank @Size(max = 120)
    private String name;

    @NotNull(message = "Property id is required")
    private Long propertyId;

    @NotNull
    private AssetCategory category;

    @Size(max = 80)
    private String make;

    @Size(max = 80)
    private String model;

    @Size(max = 120)
    private String serialNumber;

    @PastOrPresent(message = "Install date cannot be in the future")
    private LocalDate installedOn;

    private LocalDate warrantyExpiry;
}
