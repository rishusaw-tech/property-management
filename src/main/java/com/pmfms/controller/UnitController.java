package com.pmfms.controller;

import com.pmfms.api.UnitApi;
import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.unit.UnitRequest;
import com.pmfms.dto.unit.UnitResponse;
import com.pmfms.enums.UnitStatus;
import com.pmfms.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UnitController implements UnitApi {

    private final UnitService unitService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER')")
    public ResponseEntity<UnitResponse> create(UnitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unitService.create(request));
    }

    @Override
    public ResponseEntity<UnitResponse> getById(Long id) {
        return ResponseEntity.ok(unitService.getById(id));
    }

    @Override
    public ResponseEntity<PageResponse<UnitResponse>> list(Long propertyId, UnitStatus status, int page, int size) {
        return ResponseEntity.ok(unitService.list(propertyId, status, page, size));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER')")
    public ResponseEntity<UnitResponse> update(Long id, UnitRequest request) {
        return ResponseEntity.ok(unitService.update(id, request));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER','FACILITY_MANAGER')") // FM may mark UNDER_MAINTENANCE
    public ResponseEntity<UnitResponse> changeStatus(Long id, UnitStatus status) {
        return ResponseEntity.ok(unitService.changeStatus(id, status));
    }
}
