package com.pmfms.api;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.property.PropertyRequest;
import com.pmfms.dto.property.PropertyResponse;
import com.pmfms.enums.PropertyStatus;
import com.pmfms.enums.PropertyType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "3. Properties", description = "Property acquisition & onboarding lifecycle (BRD 8.1)")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/properties")
public interface PropertyApi {

    @Operation(summary = "Register a new property", description = "Created in DRAFT status; auto-generates the unique Property code.")
    @PostMapping
    ResponseEntity<PropertyResponse> create(@Valid @RequestBody PropertyRequest request);

    @Operation(summary = "Get property by id")
    @GetMapping("/{id}")
    ResponseEntity<PropertyResponse> getById(@PathVariable Long id);

    @Operation(summary = "List properties (paginated)", description = "Filter by status, type and/or city.")
    @GetMapping
    ResponseEntity<PageResponse<PropertyResponse>> list(
            @RequestParam(required = false) PropertyStatus status,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Update property details")
    @PutMapping("/{id}")
    ResponseEntity<PropertyResponse> update(@PathVariable Long id, @Valid @RequestBody PropertyRequest request);

    @Operation(summary = "Move property through onboarding workflow",
            description = "DRAFT -> PENDING_VERIFICATION -> LEGAL_REVIEW -> ACTIVE; also INACTIVE / DISPOSED (BRD 8.1 approval workflow).")
    @PatchMapping("/{id}/status")
    ResponseEntity<PropertyResponse> changeStatus(@PathVariable Long id, @RequestParam PropertyStatus status);
}
