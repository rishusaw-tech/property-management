package com.pmfms.api;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.unit.UnitRequest;
import com.pmfms.dto.unit.UnitResponse;
import com.pmfms.enums.UnitStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "4. Units", description = "Unit-level details within properties (BRD 8.1) and occupancy status (BRD 9.5)")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/units")
public interface UnitApi {

    @Operation(summary = "Add a unit to a property", description = "Created VACANT; auto-generates the unique Unit code.")
    @PostMapping
    ResponseEntity<UnitResponse> create(@Valid @RequestBody UnitRequest request);

    @Operation(summary = "Get unit by id")
    @GetMapping("/{id}")
    ResponseEntity<UnitResponse> getById(@PathVariable Long id);

    @Operation(summary = "List units (paginated)", description = "Filter by property and/or occupancy status.")
    @GetMapping
    ResponseEntity<PageResponse<UnitResponse>> list(
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) UnitStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Update unit details")
    @PutMapping("/{id}")
    ResponseEntity<UnitResponse> update(@PathVariable Long id, @Valid @RequestBody UnitRequest request);

    @Operation(summary = "Change unit occupancy status", description = "VACANT / OCCUPIED / UNDER_MAINTENANCE / BLOCKED.")
    @PatchMapping("/{id}/status")
    ResponseEntity<UnitResponse> changeStatus(@PathVariable Long id, @RequestParam UnitStatus status);
}
