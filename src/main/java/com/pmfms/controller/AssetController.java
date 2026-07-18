package com.pmfms.controller;

import com.pmfms.api.AssetApi;
import com.pmfms.dto.asset.AssetRequest;
import com.pmfms.dto.asset.AssetResponse;
import com.pmfms.dto.common.PageResponse;
import com.pmfms.enums.AssetCategory;
import com.pmfms.enums.AssetStatus;
import com.pmfms.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/** BRD 13: Asset Register - Admin Full, Facility Manager Full, PM view, Vendor view (assigned). */
@RestController
@RequiredArgsConstructor
public class AssetController implements AssetApi {

    private final AssetService assetService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<AssetResponse> create(AssetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.create(request));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER','PROPERTY_MANAGER','VENDOR')")
    public ResponseEntity<AssetResponse> getById(Long id) {
        return ResponseEntity.ok(assetService.getById(id));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER','PROPERTY_MANAGER','VENDOR')")
    public ResponseEntity<AssetResponse> getByTag(String tag) {
        return ResponseEntity.ok(assetService.getByTag(tag));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER','PROPERTY_MANAGER')")
    public ResponseEntity<PageResponse<AssetResponse>> list(Long propertyId, AssetCategory category,
                                                            AssetStatus status, int page, int size) {
        return ResponseEntity.ok(assetService.list(propertyId, category, status, page, size));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<AssetResponse> update(Long id, AssetRequest request) {
        return ResponseEntity.ok(assetService.update(id, request));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<AssetResponse> changeStatus(Long id, AssetStatus status) {
        return ResponseEntity.ok(assetService.changeStatus(id, status));
    }
}
