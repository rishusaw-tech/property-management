package com.pmfms.repository;

import com.pmfms.entity.Property;
import com.pmfms.enums.PropertyStatus;
import com.pmfms.enums.PropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("""
            SELECT p FROM Property p
            WHERE (:status IS NULL OR p.status = :status)
              AND (:type   IS NULL OR p.type   = :type)
              AND (:city   IS NULL OR LOWER(p.city) = LOWER(:city))
            """)
    Page<Property> search(@Param("status") PropertyStatus status,
                          @Param("type") PropertyType type,
                          @Param("city") String city,
                          Pageable pageable);
}
