package com.pmfms.controller;

import com.pmfms.api.PropertyApi;
import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.property.PropertyRequest;
import com.pmfms.dto.property.PropertyResponse;
import com.pmfms.enums.PropertyStatus;
import com.pmfms.enums.PropertyType;
import com.pmfms.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PropertyController implements PropertyApi {

    private final PropertyService propertyService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER')") // BRD 13: Property/Unit Master - Admin Full, PM Edit
    public ResponseEntity<PropertyResponse> create(PropertyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.create(request));
    }

    @Override
    public ResponseEntity<PropertyResponse> getById(Long id) {
        return ResponseEntity.ok(propertyService.getById(id));
    }

    @Override
    public ResponseEntity<PageResponse<PropertyResponse>> list(PropertyStatus status, PropertyType type,
                                                               String city, int page, int size) {
        return ResponseEntity.ok(propertyService.list(status, type, city, page, size));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER')")
    public ResponseEntity<PropertyResponse> update(Long id, PropertyRequest request) {
        return ResponseEntity.ok(propertyService.update(id, request));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')") // approval workflow steps are admin-controlled
    public ResponseEntity<PropertyResponse> changeStatus(Long id, PropertyStatus status) {
        return ResponseEntity.ok(propertyService.changeStatus(id, status));
    }
}
