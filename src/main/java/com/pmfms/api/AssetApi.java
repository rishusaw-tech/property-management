package com.pmfms.api;

import com.pmfms.dto.asset.AssetRequest;
import com.pmfms.dto.asset.AssetResponse;
import com.pmfms.dto.common.PageResponse;
import com.pmfms.enums.AssetCategory;
import com.pmfms.enums.AssetStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "7. Assets", description = "Central asset register with QR tag lookup (BRD 9.1)")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/assets")
public interface AssetApi {

    @Operation(summary = "Register an asset", description = "Auto-generates the QR/barcode tag.")
    @PostMapping
    ResponseEntity<AssetResponse> create(@Valid @RequestBody AssetRequest request);

    @Operation(summary = "Get asset by id")
    @GetMapping("/{id}")
    ResponseEntity<AssetResponse> getById(@PathVariable Long id);

    @Operation(summary = "Lookup asset by QR/barcode tag", description = "Field verification via mobile scan (BRD 9.1).")
    @GetMapping("/by-tag/{tag}")
    ResponseEntity<AssetResponse> getByTag(@PathVariable String tag);

    @Operation(summary = "List assets (paginated)", description = "Filter by property, category and/or status.")
    @GetMapping
    ResponseEntity<PageResponse<AssetResponse>> list(
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) AssetCategory category,
            @RequestParam(required = false) AssetStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Update asset details")
    @PutMapping("/{id}")
    ResponseEntity<AssetResponse> update(@PathVariable Long id, @Valid @RequestBody AssetRequest request);

    @Operation(summary = "Change asset status", description = "OPERATIONAL / UNDER_MAINTENANCE / OUT_OF_SERVICE / RETIRED.")
    @PatchMapping("/{id}/status")
    ResponseEntity<AssetResponse> changeStatus(@PathVariable Long id, @RequestParam AssetStatus status);
}
