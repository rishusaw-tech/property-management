package com.pmfms.api;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.lease.LeaseRenewRequest;
import com.pmfms.dto.lease.LeaseRequest;
import com.pmfms.dto.lease.LeaseResponse;
import com.pmfms.enums.LeaseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "5. Leases", description = "Lease lifecycle: Draft -> Pending Approval -> Active -> ... (BRD 8.2/8.5, 14.2)")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/leases")
public interface LeaseApi {

    @Operation(summary = "Create a lease (DRAFT)", description = "Unit must be VACANT and have no other active lease.")
    @PostMapping
    ResponseEntity<LeaseResponse> create(@Valid @RequestBody LeaseRequest request);

    @Operation(summary = "Get lease by id")
    @GetMapping("/{id}")
    ResponseEntity<LeaseResponse> getById(@PathVariable Long id);

    @Operation(summary = "List leases (paginated)", description = "Filter by status, tenant and/or unit.")
    @GetMapping
    ResponseEntity<PageResponse<LeaseResponse>> list(
            @RequestParam(required = false) LeaseStatus status,
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Long unitId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Submit lease for approval", description = "DRAFT -> PENDING_APPROVAL.")
    @PatchMapping("/{id}/submit")
    ResponseEntity<LeaseResponse> submit(@PathVariable Long id);

    @Operation(summary = "Approve & activate lease", description = "PENDING_APPROVAL -> ACTIVE. Unit becomes OCCUPIED (move-in, BRD 8.3).")
    @PatchMapping("/{id}/approve")
    ResponseEntity<LeaseResponse> approve(@PathVariable Long id);

    @Operation(summary = "Renew lease with rent revision", description = "Extends end date and applies revised rent (BRD 8.5).")
    @PatchMapping("/{id}/renew")
    ResponseEntity<LeaseResponse> renew(@PathVariable Long id, @Valid @RequestBody LeaseRenewRequest request);

    @Operation(summary = "Serve notice to vacate", description = "ACTIVE/RENEWAL_DUE -> NOTICE_SERVED (notice period validated).")
    @PatchMapping("/{id}/notice")
    ResponseEntity<LeaseResponse> serveNotice(@PathVariable Long id);

    @Operation(summary = "Terminate lease (move-out)", description = "-> TERMINATED. Unit becomes VACANT (BRD 8.6).")
    @PatchMapping("/{id}/terminate")
    ResponseEntity<LeaseResponse> terminate(@PathVariable Long id);

    @Operation(summary = "Close lease", description = "TERMINATED -> CLOSED after deposit reconciliation (BRD 8.6).")
    @PatchMapping("/{id}/close")
    ResponseEntity<LeaseResponse> close(@PathVariable Long id);
}
