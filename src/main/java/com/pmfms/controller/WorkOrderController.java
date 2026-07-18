package com.pmfms.controller;

import com.pmfms.api.WorkOrderApi;
import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.workorder.WorkOrderAssignRequest;
import com.pmfms.dto.workorder.WorkOrderRequest;
import com.pmfms.dto.workorder.WorkOrderResponse;
import com.pmfms.dto.workorder.WorkOrderTransitionRequest;
import com.pmfms.enums.WorkOrderPriority;
import com.pmfms.enums.WorkOrderStatus;
import com.pmfms.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

/** BRD 13: Work Orders - all roles can create/view; FM full; Vendor updates assigned. */
@RestController
@RequiredArgsConstructor
public class WorkOrderController implements WorkOrderApi {

    private final WorkOrderService workOrderService;

    @Override // any authenticated user (tenant, PM, FM...) can raise a ticket - BRD 9.3
    public ResponseEntity<WorkOrderResponse> create(WorkOrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(workOrderService.create(request, email));
    }

    @Override
    public ResponseEntity<WorkOrderResponse> getById(Long id) {
        return ResponseEntity.ok(workOrderService.getById(id));
    }

    @Override
    public ResponseEntity<PageResponse<WorkOrderResponse>> list(Long propertyId, WorkOrderStatus status,
                                                                WorkOrderPriority priority, Long vendorId,
                                                                int page, int size) {
        return ResponseEntity.ok(workOrderService.list(propertyId, status, priority, vendorId, page, size));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<WorkOrderResponse> assign(Long id, WorkOrderAssignRequest request) {
        return ResponseEntity.ok(workOrderService.assign(id, request));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER','VENDOR')") // vendor progresses assigned tickets
    public ResponseEntity<WorkOrderResponse> transition(Long id, WorkOrderTransitionRequest request) {
        return ResponseEntity.ok(workOrderService.transition(id, request));
    }
}
