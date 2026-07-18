package com.pmfms.controller;

import com.pmfms.api.VendorApi;
import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.vendor.VendorRatingRequest;
import com.pmfms.dto.vendor.VendorRequest;
import com.pmfms.dto.vendor.VendorResponse;
import com.pmfms.enums.VendorCategory;
import com.pmfms.enums.VendorStatus;
import com.pmfms.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/** BRD 13: Vendor Management - Admin Full, Facility Manager Edit, PM view. */
@RestController
@RequiredArgsConstructor
public class VendorController implements VendorApi {

    private final VendorService vendorService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<VendorResponse> create(VendorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendorService.create(request));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER','PROPERTY_MANAGER','VENDOR')")
    public ResponseEntity<VendorResponse> getById(Long id) {
        return ResponseEntity.ok(vendorService.getById(id));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER','PROPERTY_MANAGER')")
    public ResponseEntity<PageResponse<VendorResponse>> list(VendorCategory category, VendorStatus status,
                                                             int page, int size) {
        return ResponseEntity.ok(vendorService.list(category, status, page, size));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<VendorResponse> update(Long id, VendorRequest request) {
        return ResponseEntity.ok(vendorService.update(id, request));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<VendorResponse> updateRating(Long id, VendorRatingRequest request) {
        return ResponseEntity.ok(vendorService.updateRating(id, request));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VendorResponse> changeStatus(Long id, VendorStatus status) {
        return ResponseEntity.ok(vendorService.changeStatus(id, status));
    }
}
