package com.pmfms.service.impl;

import com.pmfms.dto.common.PageResponse;
import com.pmfms.dto.invoice.InvoiceRequest;
import com.pmfms.dto.invoice.InvoiceResponse;
import com.pmfms.dto.invoice.PaymentRequest;
import com.pmfms.dto.invoice.PaymentResponse;
import com.pmfms.entity.Invoice;
import com.pmfms.entity.Lease;
import com.pmfms.entity.Payment;
import com.pmfms.enums.InvoiceStatus;
import com.pmfms.enums.LeaseStatus;
import com.pmfms.enums.PaymentStatus;
import com.pmfms.mapper.EntityMapper;
import com.pmfms.repository.InvoiceRepository;
import com.pmfms.repository.LeaseRepository;
import com.pmfms.repository.PaymentRepository;
import com.pmfms.service.InvoiceService;
import com.pmfms.util.CodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Billing per BRD 8.4 with the invoice lifecycle of BRD 14.3 and
 * idempotent payment recording (BRD 11.3 / 15.2 - duplicate submission).
 */
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final LeaseRepository leaseRepository;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public InvoiceResponse create(InvoiceRequest request) {
        Lease lease = leaseRepository.findById(request.getLeaseId())
                .orElseThrow(() -> new EntityNotFoundException("Lease not found with id " + request.getLeaseId()));

        if (lease.getStatus() != LeaseStatus.ACTIVE && lease.getStatus() != LeaseStatus.RENEWAL_DUE
                && lease.getStatus() != LeaseStatus.NOTICE_SERVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invoices can only be raised on ACTIVE / RENEWAL_DUE / NOTICE_SERVED leases");
        }

        Invoice invoice = Invoice.builder()
                .invoiceNumber(CodeGenerator.generate("INV"))
                .lease(lease)
                .type(request.getType())
                .amount(request.getAmount())
                .amountPaid(BigDecimal.ZERO)
                .dueDate(request.getDueDate())
                .status(InvoiceStatus.GENERATED)
                .build();

        return mapper.map(invoiceRepository.save(invoice), InvoiceResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getById(Long id) {
        return mapper.map(findInvoice(id), InvoiceResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InvoiceResponse> list(Long leaseId, InvoiceStatus status, int page, int size) {
        Page<Invoice> result = invoiceRepository.search(leaseId, status,
                PageRequest.of(page, size, Sort.by("id").descending()));
        return PageResponse.of(result, mapper.mapPage(result, InvoiceResponse.class));
    }

    @Override
    @Transactional
    public PaymentResponse recordPayment(Long invoiceId, PaymentRequest request) {
        // ===== Idempotency check (BRD 11.3): same reference -> return existing =====
        Optional<Payment> existing = paymentRepository.findByReference(request.getReference());
        if (existing.isPresent()) {
            return mapper.map(existing.get(), PaymentResponse.class);
        }

        Invoice invoice = findInvoice(invoiceId);

        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.WRITTEN_OFF) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invoice is already " + invoice.getStatus());
        }

        BigDecimal outstanding = invoice.getAmount().subtract(invoice.getAmountPaid());
        if (request.getAmount().compareTo(outstanding) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Payment (" + request.getAmount() + ") exceeds outstanding amount (" + outstanding + ")");
        }

        Payment payment = Payment.builder()
                .reference(request.getReference())
                .invoice(invoice)
                .amount(request.getAmount())
                .mode(request.getMode())
                .status(PaymentStatus.SUCCESS)
                .paidAt(Instant.now())
                .build();
        payment = paymentRepository.save(payment);

        // Update invoice status per BRD 14.3
        invoice.setAmountPaid(invoice.getAmountPaid().add(request.getAmount()));
        invoice.setStatus(invoice.getAmountPaid().compareTo(invoice.getAmount()) >= 0
                ? InvoiceStatus.PAID
                : InvoiceStatus.PARTIALLY_PAID);
        invoiceRepository.save(invoice);

        return mapper.map(payment, PaymentResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> listPayments(Long invoiceId) {
        findInvoice(invoiceId); // 404 if missing
        return mapper.mapList(paymentRepository.findByInvoiceId(invoiceId), PaymentResponse.class);
    }

    /**
     * Daily job: unpaid invoices past their due date become OVERDUE
     * (BRD 8.4 dunning input / 14.3 lifecycle).
     */
    @Scheduled(cron = "${app.scheduler.invoice-overdue-cron:0 30 0 * * *}")
    @Transactional
    public void markOverdueInvoices() {
        List<Invoice> overdue = invoiceRepository.findByDueDateBeforeAndStatusIn(
                LocalDate.now(),
                List.of(InvoiceStatus.GENERATED, InvoiceStatus.SENT, InvoiceStatus.PARTIALLY_PAID));
        overdue.forEach(i -> i.setStatus(InvoiceStatus.OVERDUE));
        invoiceRepository.saveAll(overdue);
    }

    private Invoice findInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id " + id));
    }
}
