-- =========================================================
-- V3: Leasing & billing (BRD 8.2-8.5, 14.2, 14.3)
-- =========================================================

CREATE TABLE leases (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    code               VARCHAR(40)   NOT NULL,
    unit_id            BIGINT        NOT NULL,
    tenant_id          BIGINT        NOT NULL,
    start_date         DATE          NOT NULL,
    end_date           DATE          NOT NULL,
    monthly_rent       DECIMAL(14,2) NOT NULL,
    security_deposit   DECIMAL(14,2) NOT NULL,
    billing_cycle      VARCHAR(20)   NOT NULL,
    status             VARCHAR(30)   NOT NULL,
    notice_period_days INT           NOT NULL,
    created_at         TIMESTAMP     NOT NULL,
    updated_at         TIMESTAMP,
    CONSTRAINT uk_leases_code UNIQUE (code),
    CONSTRAINT fk_leases_unit   FOREIGN KEY (unit_id)   REFERENCES units (id),
    CONSTRAINT fk_leases_tenant FOREIGN KEY (tenant_id) REFERENCES users (id)
);
CREATE INDEX idx_leases_code   ON leases (code);
CREATE INDEX idx_leases_unit   ON leases (unit_id);
CREATE INDEX idx_leases_tenant ON leases (tenant_id);
CREATE INDEX idx_leases_status ON leases (status);

CREATE TABLE invoices (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(40)   NOT NULL,
    lease_id       BIGINT        NOT NULL,
    type           VARCHAR(20)   NOT NULL,
    amount         DECIMAL(14,2) NOT NULL,
    amount_paid    DECIMAL(14,2) NOT NULL DEFAULT 0,
    due_date       DATE          NOT NULL,
    status         VARCHAR(30)   NOT NULL,
    created_at     TIMESTAMP     NOT NULL,
    CONSTRAINT uk_invoices_number UNIQUE (invoice_number),
    CONSTRAINT fk_invoices_lease FOREIGN KEY (lease_id) REFERENCES leases (id)
);
CREATE INDEX idx_invoices_number   ON invoices (invoice_number);
CREATE INDEX idx_invoices_lease    ON invoices (lease_id);
CREATE INDEX idx_invoices_status   ON invoices (status);
CREATE INDEX idx_invoices_due_date ON invoices (due_date);

CREATE TABLE payments (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference  VARCHAR(60)   NOT NULL,
    invoice_id BIGINT        NOT NULL,
    amount     DECIMAL(14,2) NOT NULL,
    mode       VARCHAR(30)   NOT NULL,
    status     VARCHAR(20)   NOT NULL,
    paid_at    TIMESTAMP     NOT NULL,
    created_at TIMESTAMP     NOT NULL,
    CONSTRAINT uk_payments_reference UNIQUE (reference),
    CONSTRAINT fk_payments_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id)
);
CREATE INDEX idx_payments_reference ON payments (reference);
CREATE INDEX idx_payments_invoice   ON payments (invoice_id);
