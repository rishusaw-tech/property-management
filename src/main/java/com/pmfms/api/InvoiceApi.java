package com.pmfms.api;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.invoice.InvoiceRequest;
import com.pmfms.dto.invoice.InvoiceResponse;
import com.pmfms.dto.invoice.PaymentRequest;
import com.pmfms.dto.invoice.PaymentResponse;
import com.pmfms.enums.InvoiceStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "6. Billing", description = "Invoices & payments - Rent, CAM, utilities, late fees (BRD 8.4, 14.3)")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/invoices")
public interface InvoiceApi {

    @Operation(summary = "Generate an invoice against a lease", description = "Lease must be ACTIVE or RENEWAL_DUE.")
    @PostMapping
    ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceRequest request);

    @Operation(summary = "Get invoice by id")
    @GetMapping("/{id}")
    ResponseEntity<InvoiceResponse> getById(@PathVariable Long id);

    @Operation(summary = "List invoices (paginated)", description = "Filter by lease and/or status.")
    @GetMapping
    ResponseEntity<PageResponse<InvoiceResponse>> list(
            @RequestParam(required = false) Long leaseId,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Record a payment against an invoice",
            description = "Idempotent by 'reference' (BRD 11.3): resubmitting the same reference returns the existing payment instead of double-charging. Invoice status auto-moves to PARTIALLY_PAID/PAID.")
    @PostMapping("/{id}/payments")
    ResponseEntity<PaymentResponse> recordPayment(@PathVariable Long id, @Valid @RequestBody PaymentRequest request);

    @Operation(summary = "List payments of an invoice")
    @GetMapping("/{id}/payments")
    ResponseEntity<List<PaymentResponse>> listPayments(@PathVariable Long id);
}
