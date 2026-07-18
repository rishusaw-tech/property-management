package com.pmfms.repository;

import com.pmfms.entity.Unit;
import com.pmfms.enums.UnitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UnitRepository extends JpaRepository<Unit, Long> {

    @Query("""
            SELECT u FROM Unit u
            WHERE (:propertyId IS NULL OR u.property.id = :propertyId)
              AND (:status     IS NULL OR u.status = :status)
            """)
    Page<Unit> search(@Param("propertyId") Long propertyId,
                      @Param("status") UnitStatus status,
                      Pageable pageable);

    long countByPropertyId(Long propertyId);

    long countByPropertyIdAndStatus(Long propertyId, UnitStatus status);
}
