-- =========================================================
-- V4: Facility management - assets, vendors, work orders,
--     compliance (BRD 9.1, 9.3, 9.4, 9.6, 14.1)
-- =========================================================

CREATE TABLE assets (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag             VARCHAR(40)  NOT NULL,
    name            VARCHAR(120) NOT NULL,
    property_id     BIGINT       NOT NULL,
    category        VARCHAR(30)  NOT NULL,
    make            VARCHAR(80),
    model           VARCHAR(80),
    serial_number   VARCHAR(120),
    installed_on    DATE,
    warranty_expiry DATE,
    status          VARCHAR(30)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP,
    CONSTRAINT uk_assets_tag UNIQUE (tag),
    CONSTRAINT fk_assets_property FOREIGN KEY (property_id) REFERENCES properties (id) ON DELETE CASCADE
);
CREATE INDEX idx_assets_tag      ON assets (tag);
CREATE INDEX idx_assets_property ON assets (property_id);
CREATE INDEX idx_assets_status   ON assets (status);

CREATE TABLE vendors (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name   VARCHAR(160) NOT NULL,
    category       VARCHAR(30)  NOT NULL,
    contact_name   VARCHAR(120),
    contact_email  VARCHAR(160) NOT NULL,
    contact_phone  VARCHAR(20),
    rating         DECIMAL(3,2),
    status         VARCHAR(20)  NOT NULL,
    contract_start DATE,
    contract_end   DATE,
    user_id        BIGINT,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP,
    CONSTRAINT fk_vendors_user FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE INDEX idx_vendors_status   ON vendors (status);
CREATE INDEX idx_vendors_category ON vendors (category);

CREATE TABLE work_orders (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    number             VARCHAR(40)   NOT NULL,
    type               VARCHAR(20)   NOT NULL,
    priority           VARCHAR(10)   NOT NULL,
    status             VARCHAR(20)   NOT NULL,
    title              VARCHAR(160)  NOT NULL,
    description        VARCHAR(2000),
    property_id        BIGINT        NOT NULL,
    unit_id            BIGINT,
    asset_id           BIGINT,
    raised_by_id       BIGINT        NOT NULL,
    assigned_vendor_id BIGINT,
    sla_due_at         TIMESTAMP     NOT NULL,
    acknowledged_at    TIMESTAMP,
    resolved_at        TIMESTAMP,
    closed_at          TIMESTAMP,
    created_at         TIMESTAMP     NOT NULL,
    updated_at         TIMESTAMP,
    CONSTRAINT uk_work_orders_number UNIQUE (number),
    CONSTRAINT fk_wo_property FOREIGN KEY (property_id)        REFERENCES properties (id),
    CONSTRAINT fk_wo_unit     FOREIGN KEY (unit_id)            REFERENCES units (id),
    CONSTRAINT fk_wo_asset    FOREIGN KEY (asset_id)           REFERENCES assets (id),
    CONSTRAINT fk_wo_raiser   FOREIGN KEY (raised_by_id)       REFERENCES users (id),
    CONSTRAINT fk_wo_vendor   FOREIGN KEY (assigned_vendor_id) REFERENCES vendors (id)
);
CREATE INDEX idx_wo_number   ON work_orders (number);
CREATE INDEX idx_wo_property ON work_orders (property_id);
CREATE INDEX idx_wo_status   ON work_orders (status);
CREATE INDEX idx_wo_priority ON work_orders (priority);
CREATE INDEX idx_wo_sla_due  ON work_orders (sla_due_at);

CREATE TABLE compliance_records (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_id        BIGINT      NOT NULL,
    certificate_type   VARCHAR(40) NOT NULL,
    certificate_number VARCHAR(80) NOT NULL,
    issued_by          VARCHAR(120),
    issue_date         DATE        NOT NULL,
    expiry_date        DATE        NOT NULL,
    status             VARCHAR(20) NOT NULL,
    created_at         TIMESTAMP   NOT NULL,
    CONSTRAINT fk_compliance_property FOREIGN KEY (property_id) REFERENCES properties (id) ON DELETE CASCADE
);
CREATE INDEX idx_compliance_property ON compliance_records (property_id);
CREATE INDEX idx_compliance_expiry   ON compliance_records (expiry_date);
CREATE INDEX idx_compliance_status   ON compliance_records (status);
