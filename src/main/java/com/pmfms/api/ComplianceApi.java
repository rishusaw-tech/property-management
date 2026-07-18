package com.pmfms.api;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.compliance.ComplianceRecordRequest;
import com.pmfms.dto.compliance.ComplianceRecordResponse;
import com.pmfms.enums.ComplianceStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "10. Compliance", description = "Statutory certificate tracker with expiry alerts (BRD 9.6)")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/compliance-records")
public interface ComplianceApi {

    @Operation(summary = "Register a compliance certificate", description = "Status auto-derived: VALID / EXPIRING_SOON (<=30 days) / EXPIRED.")
    @PostMapping
    ResponseEntity<ComplianceRecordResponse> create(@Valid @RequestBody ComplianceRecordRequest request);

    @Operation(summary = "Get compliance record by id")
    @GetMapping("/{id}")
    ResponseEntity<ComplianceRecordResponse> getById(@PathVariable Long id);

    @Operation(summary = "List compliance records (paginated)", description = "Filter by property and/or status.")
    @GetMapping
    ResponseEntity<PageResponse<ComplianceRecordResponse>> list(
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) ComplianceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);
}
