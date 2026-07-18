package com.pmfms.controller;

import com.pmfms.api.InvoiceApi;
import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.invoice.InvoiceRequest;
import com.pmfms.dto.invoice.InvoiceResponse;
import com.pmfms.dto.invoice.PaymentRequest;
import com.pmfms.dto.invoice.PaymentResponse;
import com.pmfms.enums.InvoiceStatus;
import com.pmfms.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InvoiceController implements InvoiceApi {

    private final InvoiceService invoiceService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER')")
    public ResponseEntity<InvoiceResponse> create(InvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.create(request));
    }

    @Override
    public ResponseEntity<InvoiceResponse> getById(Long id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @Override
    public ResponseEntity<PageResponse<InvoiceResponse>> list(Long leaseId, InvoiceStatus status, int page, int size) {
        return ResponseEntity.ok(invoiceService.list(leaseId, status, page, size));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','PROPERTY_MANAGER','TENANT')") // tenants pay their own invoices online
    public ResponseEntity<PaymentResponse> recordPayment(Long id, PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.recordPayment(id, request));
    }

    @Override
    public ResponseEntity<List<PaymentResponse>> listPayments(Long id) {
        return ResponseEntity.ok(invoiceService.listPayments(id));
    }
}
