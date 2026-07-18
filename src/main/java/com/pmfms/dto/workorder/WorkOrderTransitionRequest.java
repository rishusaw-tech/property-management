package com.pmfms.dto.workorder;

import com.pmfms.enums.WorkOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Move a work order through its lifecycle (BRD 14.1). Invalid jumps are rejected. */
@Data
public class WorkOrderTransitionRequest {

    @NotNull
    @Schema(example = "IN_PROGRESS")
    private WorkOrderStatus targetStatus;

    @Size(max = 500)
    private String remark;
}
