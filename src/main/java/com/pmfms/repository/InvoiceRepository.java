package com.pmfms.repository;

import com.pmfms.entity.Invoice;
import com.pmfms.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("""
            SELECT i FROM Invoice i
            WHERE (:leaseId IS NULL OR i.lease.id = :leaseId)
              AND (:status  IS NULL OR i.status = :status)
            """)
    Page<Invoice> search(@Param("leaseId") Long leaseId,
                         @Param("status") InvoiceStatus status,
                         Pageable pageable);

    /** Unpaid invoices past due - flipped to OVERDUE by the scheduler (BRD 14.3). */
    List<Invoice> findByDueDateBeforeAndStatusIn(LocalDate date, List<InvoiceStatus> statuses);
}
