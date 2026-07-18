package com.pmfms.repository;

import com.pmfms.entity.WorkOrder;
import com.pmfms.enums.WorkOrderPriority;
import com.pmfms.enums.WorkOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    @Query("""
            SELECT w FROM WorkOrder w
            WHERE (:propertyId IS NULL OR w.property.id = :propertyId)
              AND (:status     IS NULL OR w.status = :status)
              AND (:priority   IS NULL OR w.priority = :priority)
              AND (:vendorId   IS NULL OR w.assignedVendor.id = :vendorId)
            """)
    Page<WorkOrder> search(@Param("propertyId") Long propertyId,
                           @Param("status") WorkOrderStatus status,
                           @Param("priority") WorkOrderPriority priority,
                           @Param("vendorId") Long vendorId,
                           Pageable pageable);
}
