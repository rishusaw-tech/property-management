package com.pmfms.controller;

import com.pmfms.api.LeaseApi;
import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.lease.LeaseRenewRequest;
import com.pmfms.dto.lease.LeaseRequest;
import com.pmfms.dto.lease.LeaseResponse;
import com.pmfms.enums.LeaseStatus;
import com.pmfms.service.LeaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/** BRD 13: Leasing & Billing - Admin Full, Property Manager Full, others view. */
@RestController
@RequiredArgsConstructor
public class LeaseController implements LeaseApi {

    private final LeaseService leaseService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER')")
    public ResponseEntity<LeaseResponse> create(LeaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaseService.create(request));
    }

    @Override
    public ResponseEntity<LeaseResponse> getById(Long id) {
        return ResponseEntity.ok(leaseService.getById(id));
    }

    @Override
    public ResponseEntity<PageResponse<LeaseResponse>> list(LeaseStatus status, Long tenantId,
                                                            Long unitId, int page, int size) {
        return ResponseEntity.ok(leaseService.list(status, tenantId, unitId, page, size));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER')")
    public ResponseEntity<LeaseResponse> submit(Long id) {
        return ResponseEntity.ok(leaseService.submit(id));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER')")
    public ResponseEntity<LeaseResponse> approve(Long id) {
        return ResponseEntity.ok(leaseService.approve(id));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER')")
    public ResponseEntity<LeaseResponse> renew(Long id, LeaseRenewRequest request) {
        return ResponseEntity.ok(leaseService.renew(id, request));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER','TENANT')") // tenant may serve notice on own lease
    public ResponseEntity<LeaseResponse> serveNotice(Long id) {
        return ResponseEntity.ok(leaseService.serveNotice(id));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER')")
    public ResponseEntity<LeaseResponse> terminate(Long id) {
        return ResponseEntity.ok(leaseService.terminate(id));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER')")
    public ResponseEntity<LeaseResponse> close(Long id) {
        return ResponseEntity.ok(leaseService.close(id));
    }
}
