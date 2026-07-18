package com.pmfms.repository;

import com.pmfms.entity.Lease;
import com.pmfms.enums.LeaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaseRepository extends JpaRepository<Lease, Long> {

    @Query("""
            SELECT l FROM Lease l
            WHERE (:status   IS NULL OR l.status = :status)
              AND (:tenantId IS NULL OR l.tenant.id = :tenantId)
              AND (:unitId   IS NULL OR l.unit.id = :unitId)
            """)
    Page<Lease> search(@Param("status") LeaseStatus status,
                       @Param("tenantId") Long tenantId,
                       @Param("unitId") Long unitId,
                       Pageable pageable);

    boolean existsByUnitIdAndStatusIn(Long unitId, List<LeaseStatus> statuses);

    /** Active leases whose end date falls within the renewal alert window (BRD 8.5). */
    List<Lease> findByStatusAndEndDateBetween(LeaseStatus status, LocalDate from, LocalDate to);
}
