package com.pmfms.repository;

import com.pmfms.entity.Asset;
import com.pmfms.enums.AssetCategory;
import com.pmfms.enums.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByTag(String tag);

    @Query("""
            SELECT a FROM Asset a
            WHERE (:propertyId IS NULL OR a.property.id = :propertyId)
              AND (:category   IS NULL OR a.category = :category)
              AND (:status     IS NULL OR a.status = :status)
            """)
    Page<Asset> search(@Param("propertyId") Long propertyId,
                       @Param("category") AssetCategory category,
                       @Param("status") AssetStatus status,
                       Pageable pageable);
}
