package com.pmfms.enums;

/** Invoice/Payment lifecycle - BRD 14.3. */
public enum InvoiceStatus {
    GENERATED,
    SENT,
    PARTIALLY_PAID,
    PAID,
    OVERDUE,
    ESCALATED,
    WRITTEN_OFF
}
