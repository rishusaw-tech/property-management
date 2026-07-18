package com.pmfms.api;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.workorder.WorkOrderAssignRequest;
import com.pmfms.dto.workorder.WorkOrderRequest;
import com.pmfms.dto.workorder.WorkOrderResponse;
import com.pmfms.dto.workorder.WorkOrderTransitionRequest;
import com.pmfms.enums.WorkOrderPriority;
import com.pmfms.enums.WorkOrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "9. Work Orders", description = "Preventive & corrective maintenance ticketing with SLA timers (BRD 9.2/9.3, 14.1)")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/work-orders")
public interface WorkOrderApi {

    @Operation(summary = "Raise a work order / ticket",
            description = "Created in NEW status. SLA due time is auto-computed from priority (Critical/High/Medium/Low).")
    @PostMapping
    ResponseEntity<WorkOrderResponse> create(@Valid @RequestBody WorkOrderRequest request);

    @Operation(summary = "Get work order by id")
    @GetMapping("/{id}")
    ResponseEntity<WorkOrderResponse> getById(@PathVariable Long id);

    @Operation(summary = "List work orders (paginated)", description = "Filter by property, status, priority and/or assigned vendor.")
    @GetMapping
    ResponseEntity<PageResponse<WorkOrderResponse>> list(
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) WorkOrderStatus status,
            @RequestParam(required = false) WorkOrderPriority priority,
            @RequestParam(required = false) Long vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Assign a vendor", description = "NEW/REOPENED -> ASSIGNED.")
    @PatchMapping("/{id}/assign")
    ResponseEntity<WorkOrderResponse> assign(@PathVariable Long id, @Valid @RequestBody WorkOrderAssignRequest request);

    @Operation(summary = "Transition work order status",
            description = "Valid moves (BRD 14.1): ASSIGNED->ACKNOWLEDGED->IN_PROGRESS->(ON_HOLD)->RESOLVED->CLOSED, CLOSED/RESOLVED->REOPENED. Invalid jumps return 400.")
    @PatchMapping("/{id}/transition")
    ResponseEntity<WorkOrderResponse> transition(@PathVariable Long id, @Valid @RequestBody WorkOrderTransitionRequest request);
}
