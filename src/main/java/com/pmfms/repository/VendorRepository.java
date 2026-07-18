package com.pmfms.repository;

import com.pmfms.entity.Vendor;
import com.pmfms.enums.VendorCategory;
import com.pmfms.enums.VendorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

    @Query("""
            SELECT v FROM Vendor v
            WHERE (:category IS NULL OR v.category = :category)
              AND (:status   IS NULL OR v.status = :status)
            """)
    Page<Vendor> search(@Param("category") VendorCategory category,
                        @Param("status") VendorStatus status,
                        Pageable pageable);
}
