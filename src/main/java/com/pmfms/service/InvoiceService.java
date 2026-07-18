package com.pmfms.service;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.invoice.InvoiceRequest;
import com.pmfms.dto.invoice.InvoiceResponse;
import com.pmfms.dto.invoice.PaymentRequest;
import com.pmfms.dto.invoice.PaymentResponse;
import com.pmfms.enums.InvoiceStatus;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse create(InvoiceRequest request);

    InvoiceResponse getById(Long id);

    PageResponse<InvoiceResponse> list(Long leaseId, InvoiceStatus status, int page, int size);

    PaymentResponse recordPayment(Long invoiceId, PaymentRequest request);

    List<PaymentResponse> listPayments(Long invoiceId);
}
