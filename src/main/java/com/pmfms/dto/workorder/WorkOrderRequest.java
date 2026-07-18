package com.pmfms.dto.workorder;

import com.pmfms.enums.WorkOrderPriority;
import com.pmfms.enums.WorkOrderType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class WorkOrderRequest {

    @NotNull
    private WorkOrderType type;

    @NotNull(message = "Priority is required - it drives the SLA timer")
    private WorkOrderPriority priority;

    @NotBlank @Size(max = 160)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "Property id is required")
    private Long propertyId;

    /** Optional - a specific unit within the property. */
    private Long unitId;

    /** Optional - a specific asset (e.g. Elevator #2). */
    private Long assetId;
}
