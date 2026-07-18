package com.pmfms.dto.asset;

import com.pmfms.enums.AssetCategory;
import com.pmfms.enums.AssetStatus;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class AssetResponse {
    private Long id;
    private String tag;
    private String name;
    private Long propertyId;
    private String propertyName;
    private AssetCategory category;
    private String make;
    private String model;
    private String serialNumber;
    private LocalDate installedOn;
    private LocalDate warrantyExpiry;
    private AssetStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
