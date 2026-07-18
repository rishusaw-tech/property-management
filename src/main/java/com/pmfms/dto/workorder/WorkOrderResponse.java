package com.pmfms.dto.workorder;

import com.pmfms.enums.WorkOrderPriority;
import com.pmfms.enums.WorkOrderStatus;
import com.pmfms.enums.WorkOrderType;
import lombok.Data;

import java.time.Instant;

@Data
public class WorkOrderResponse {
    private Long id;
    private String number;
    private WorkOrderType type;
    private WorkOrderPriority priority;
    private WorkOrderStatus status;
    private String title;
    private String description;
    private Long propertyId;
    private String propertyName;
    private Long unitId;
    private String unitCode;
    private Long assetId;
    private String assetTag;
    private Long raisedById;
    private String raisedByFullName;
    private Long assignedVendorId;
    private String assignedVendorCompanyName;
    private Instant slaDueAt;
    private Boolean slaBreached;
    private Instant acknowledgedAt;
    private Instant resolvedAt;
    private Instant closedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
