package com.pmfms.repository;

import com.pmfms.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /** Idempotency check - same reference never processed twice (BRD 11.3/15.2). */
    Optional<Payment> findByReference(String reference);

    List<Payment> findByInvoiceId(Long invoiceId);
}
