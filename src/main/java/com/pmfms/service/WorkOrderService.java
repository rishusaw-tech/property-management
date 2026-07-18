package com.pmfms.service;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.workorder.WorkOrderAssignRequest;
import com.pmfms.dto.workorder.WorkOrderRequest;
import com.pmfms.dto.workorder.WorkOrderResponse;
import com.pmfms.dto.workorder.WorkOrderTransitionRequest;
import com.pmfms.enums.WorkOrderPriority;
import com.pmfms.enums.WorkOrderStatus;

public interface WorkOrderService {

    WorkOrderResponse create(WorkOrderRequest request, String raisedByEmail);

    WorkOrderResponse getById(Long id);

    PageResponse<WorkOrderResponse> list(Long propertyId, WorkOrderStatus status,
                                         WorkOrderPriority priority, Long vendorId, int page, int size);

    WorkOrderResponse assign(Long id, WorkOrderAssignRequest request);

    WorkOrderResponse transition(Long id, WorkOrderTransitionRequest request);
}
