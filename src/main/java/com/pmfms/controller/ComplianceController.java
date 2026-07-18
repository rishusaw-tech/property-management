package com.pmfms.controller;

import com.pmfms.api.ComplianceApi;
import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.compliance.ComplianceRecordRequest;
import com.pmfms.dto.compliance.ComplianceRecordResponse;
import com.pmfms.enums.ComplianceStatus;
import com.pmfms.service.ComplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ComplianceController implements ComplianceApi {

    private final ComplianceService complianceService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<ComplianceRecordResponse> create(ComplianceRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(complianceService.create(request));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER','PROPERTY_MANAGER')")
    public ResponseEntity<ComplianceRecordResponse> getById(Long id) {
        return ResponseEntity.ok(complianceService.getById(id));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER','PROPERTY_MANAGER')")
    public ResponseEntity<PageResponse<ComplianceRecordResponse>> list(Long propertyId, ComplianceStatus status,
                                                                       int page, int size) {
        return ResponseEntity.ok(complianceService.list(propertyId, status, page, size));
    }
}
