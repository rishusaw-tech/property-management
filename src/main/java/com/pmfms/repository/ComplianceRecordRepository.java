package com.pmfms.repository;

import com.pmfms.entity.ComplianceRecord;
import com.pmfms.enums.ComplianceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {

    @Query("""
            SELECT c FROM ComplianceRecord c
            WHERE (:propertyId IS NULL OR c.property.id = :propertyId)
              AND (:status     IS NULL OR c.status = :status)
            """)
    Page<ComplianceRecord> search(@Param("propertyId") Long propertyId,
                                  @Param("status") ComplianceStatus status,
                                  Pageable pageable);

    List<ComplianceRecord> findAll();
}
