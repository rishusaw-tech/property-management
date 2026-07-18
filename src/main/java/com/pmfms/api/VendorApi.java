package com.pmfms.api;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.vendor.VendorRatingRequest;
import com.pmfms.dto.vendor.VendorRequest;
import com.pmfms.dto.vendor.VendorResponse;
import com.pmfms.enums.VendorCategory;
import com.pmfms.enums.VendorStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "8. Vendors", description = "Vendor & contractor management with performance rating (BRD 9.4)")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/vendors")
public interface VendorApi {

    @Operation(summary = "Onboard a vendor")
    @PostMapping
    ResponseEntity<VendorResponse> create(@Valid @RequestBody VendorRequest request);

    @Operation(summary = "Get vendor by id")
    @GetMapping("/{id}")
    ResponseEntity<VendorResponse> getById(@PathVariable Long id);

    @Operation(summary = "List vendors (paginated)", description = "Filter by category and/or status.")
    @GetMapping
    ResponseEntity<PageResponse<VendorResponse>> list(
            @RequestParam(required = false) VendorCategory category,
            @RequestParam(required = false) VendorStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Update vendor details")
    @PutMapping("/{id}")
    ResponseEntity<VendorResponse> update(@PathVariable Long id, @Valid @RequestBody VendorRequest request);

    @Operation(summary = "Update vendor performance rating", description = "SLA scorecard 0.00 - 5.00 (BRD 9.4).")
    @PatchMapping("/{id}/rating")
    ResponseEntity<VendorResponse> updateRating(@PathVariable Long id, @Valid @RequestBody VendorRatingRequest request);

    @Operation(summary = "Change vendor status", description = "ACTIVE / INACTIVE / BLACKLISTED.")
    @PatchMapping("/{id}/status")
    ResponseEntity<VendorResponse> changeStatus(@PathVariable Long id, @RequestParam VendorStatus status);
}
