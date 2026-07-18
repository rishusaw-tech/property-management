package com.pmfms.service.impl;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.workorder.WorkOrderAssignRequest;
import com.pmfms.dto.workorder.WorkOrderRequest;
import com.pmfms.dto.workorder.WorkOrderResponse;
import com.pmfms.dto.workorder.WorkOrderTransitionRequest;
import com.pmfms.entity.*;
import com.pmfms.enums.VendorStatus;
import com.pmfms.enums.WorkOrderPriority;
import com.pmfms.enums.WorkOrderStatus;
import com.pmfms.mapper.EntityMapper;
import com.pmfms.repository.*;
import com.pmfms.service.WorkOrderService;
import com.pmfms.util.CodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static com.pmfms.enums.WorkOrderStatus.*;

/**
 * Work order lifecycle per BRD 14.1 with SLA timers per priority (BRD 9.3).
 * Allowed transitions:
 *   NEW -> ASSIGNED -> ACKNOWLEDGED -> IN_PROGRESS -> (ON_HOLD <->) -> RESOLVED -> CLOSED
 *   RESOLVED/CLOSED -> REOPENED -> ASSIGNED
 */
@Service
@RequiredArgsConstructor
public class WorkOrderServiceImpl implements WorkOrderService {

    private static final Map<WorkOrderStatus, Set<WorkOrderStatus>> ALLOWED = new EnumMap<>(Map.of(
            NEW,          Set.of(ASSIGNED),
            ASSIGNED,     Set.of(ACKNOWLEDGED),
            ACKNOWLEDGED, Set.of(IN_PROGRESS),
            IN_PROGRESS,  Set.of(ON_HOLD, RESOLVED),
            ON_HOLD,      Set.of(IN_PROGRESS),
            RESOLVED,     Set.of(CLOSED, REOPENED),
            CLOSED,       Set.of(REOPENED),
            REOPENED,     Set.of(ASSIGNED)
    ));

    private final WorkOrderRepository workOrderRepository;
    private final PropertyRepository propertyRepository;
    private final UnitRepository unitRepository;
    private final AssetRepository assetRepository;
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Value("${app.sla.critical-hours}")
    private long slaCriticalHours;

    @Value("${app.sla.high-hours}")
    private long slaHighHours;

    @Value("${app.sla.medium-hours}")
    private long slaMediumHours;

    @Value("${app.sla.low-hours}")
    private long slaLowHours;

    @Override
    @Transactional
    public WorkOrderResponse create(WorkOrderRequest request, String raisedByEmail) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id " + request.getPropertyId()));

        User raisedBy = userRepository.findByEmail(raisedByEmail)
                .orElseThrow(() -> new EntityNotFoundException("Raising user not found"));

        Unit unit = null;
        if (request.getUnitId() != null) {
            unit = unitRepository.findById(request.getUnitId())
                    .orElseThrow(() -> new EntityNotFoundException("Unit not found with id " + request.getUnitId()));
            if (!unit.getProperty().getId().equals(property.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unit does not belong to the given property");
            }
        }

        Asset asset = null;
        if (request.getAssetId() != null) {
            asset = assetRepository.findById(request.getAssetId())
                    .orElseThrow(() -> new EntityNotFoundException("Asset not found with id " + request.getAssetId()));
            if (!asset.getProperty().getId().equals(property.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Asset does not belong to the given property");
            }
        }

        WorkOrder wo = WorkOrder.builder()
                .number(CodeGenerator.generate("WO"))
                .type(request.getType())
                .priority(request.getPriority())
                .status(NEW)
                .title(request.getTitle())
                .description(request.getDescription())
                .property(property)
                .unit(unit)
                .asset(asset)
                .raisedBy(raisedBy)
                .slaDueAt(Instant.now().plus(slaFor(request.getPriority()))) // SLA timer starts now
                .build();

        return toResponse(workOrderRepository.save(wo));
    }

    @Override
    @Transactional(readOnly = true)
    public WorkOrderResponse getById(Long id) {
        return toResponse(findWorkOrder(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WorkOrderResponse> list(Long propertyId, WorkOrderStatus status,
                                                WorkOrderPriority priority, Long vendorId, int page, int size) {
        Page<WorkOrder> result = workOrderRepository.search(propertyId, status, priority, vendorId,
                PageRequest.of(page, size, Sort.by("id").descending()));
        return PageResponse.of(result,
                result.getContent().stream().map(this::toResponse).toList());
    }

    @Override
    @Transactional
    public WorkOrderResponse assign(Long id, WorkOrderAssignRequest request) {
        WorkOrder wo = findWorkOrder(id);

        if (wo.getStatus() != NEW && wo.getStatus() != REOPENED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only NEW or REOPENED work orders can be assigned (current: " + wo.getStatus() + ")");
        }

        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found with id " + request.getVendorId()));
        if (vendor.getStatus() != VendorStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vendor is not ACTIVE");
        }

        wo.setAssignedVendor(vendor);
        wo.setStatus(ASSIGNED);
        return toResponse(workOrderRepository.save(wo));
    }

    @Override
    @Transactional
    public WorkOrderResponse transition(Long id, WorkOrderTransitionRequest request) {
        WorkOrder wo = findWorkOrder(id);
        WorkOrderStatus target = request.getTargetStatus();

        Set<WorkOrderStatus> allowed = ALLOWED.getOrDefault(wo.getStatus(), Set.of());
        if (!allowed.contains(target)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid transition " + wo.getStatus() + " -> " + target + ". Allowed: " + allowed);
        }
        if (target == ASSIGNED && wo.getAssignedVendor() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Use the /assign endpoint to assign a vendor");
        }

        switch (target) {
            case ACKNOWLEDGED -> wo.setAcknowledgedAt(Instant.now());
            case RESOLVED     -> wo.setResolvedAt(Instant.now());
            case CLOSED       -> wo.setClosedAt(Instant.now());
            case REOPENED     -> { wo.setResolvedAt(null); wo.setClosedAt(null); }
            default -> { }
        }

        wo.setStatus(target);
        return toResponse(workOrderRepository.save(wo));
    }

    // ===== helpers =====

    private Duration slaFor(WorkOrderPriority priority) {
        return switch (priority) {
            case CRITICAL -> Duration.ofHours(slaCriticalHours);
            case HIGH     -> Duration.ofHours(slaHighHours);
            case MEDIUM   -> Duration.ofHours(slaMediumHours);
            case LOW      -> Duration.ofHours(slaLowHours);
        };
    }

    /** Maps entity -> DTO via the shared mapper, then computes the SLA-breach flag. */
    private WorkOrderResponse toResponse(WorkOrder wo) {
        WorkOrderResponse response = mapper.map(wo, WorkOrderResponse.class);
        Instant referencePoint = wo.getResolvedAt() != null ? wo.getResolvedAt() : Instant.now();
        boolean stillOpen = wo.getStatus() != RESOLVED && wo.getStatus() != CLOSED;
        response.setSlaBreached(stillOpen
                ? Instant.now().isAfter(wo.getSlaDueAt())
                : referencePoint.isAfter(wo.getSlaDueAt()));
        return response;
    }

    private WorkOrder findWorkOrder(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Work order not found with id " + id));
    }
}
