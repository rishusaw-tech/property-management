package com.pmfms.enums;

/** Lease lifecycle - BRD 14.2. */
public enum LeaseStatus {
    DRAFT,
    PENDING_APPROVAL,
    ACTIVE,
    RENEWAL_DUE,
    NOTICE_SERVED,
    TERMINATED,
    CLOSED
}
