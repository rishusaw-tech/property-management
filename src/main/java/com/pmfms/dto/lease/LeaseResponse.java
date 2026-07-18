package com.pmfms.dto.lease;

import com.pmfms.enums.BillingCycle;
import com.pmfms.enums.LeaseStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class LeaseResponse {
    private Long id;
    private String code;
    private Long unitId;
    private String unitCode;
    private Long tenantId;
    private String tenantFullName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal monthlyRent;
    private BigDecimal securityDeposit;
    private BillingCycle billingCycle;
    private LeaseStatus status;
    private Integer noticePeriodDays;
    private Instant createdAt;
    private Instant updatedAt;
}
