package com.pmfms.dto.workorder;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkOrderAssignRequest {

    @NotNull(message = "Vendor id is required")
    private Long vendorId;
}
