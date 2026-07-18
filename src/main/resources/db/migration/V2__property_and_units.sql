-- =========================================================
-- V2: Property & Unit master (BRD 8.1)
-- =========================================================

CREATE TABLE properties (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    code         VARCHAR(40)   NOT NULL,
    name         VARCHAR(160)  NOT NULL,
    address_line VARCHAR(255)  NOT NULL,
    city         VARCHAR(80)   NOT NULL,
    state        VARCHAR(80),
    country      VARCHAR(80)   NOT NULL,
    postal_code  VARCHAR(20),
    latitude     DECIMAL(9,6),
    longitude    DECIMAL(9,6),
    type         VARCHAR(30)   NOT NULL,
    status       VARCHAR(30)   NOT NULL,
    owner_id     BIGINT        NOT NULL,
    created_at   TIMESTAMP     NOT NULL,
    updated_at   TIMESTAMP,
    CONSTRAINT uk_properties_code UNIQUE (code),
    CONSTRAINT fk_properties_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);
CREATE INDEX idx_properties_code   ON properties (code);
CREATE INDEX idx_properties_status ON properties (status);
CREATE INDEX idx_properties_city   ON properties (city);

CREATE TABLE units (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    code         VARCHAR(40)    NOT NULL,
    property_id  BIGINT         NOT NULL,
    floor_number INT,
    area_sqft    DECIMAL(10,2)  NOT NULL,
    bedrooms     INT,
    bathrooms    INT,
    monthly_rent DECIMAL(14,2),
    status       VARCHAR(30)    NOT NULL,
    created_at   TIMESTAMP      NOT NULL,
    updated_at   TIMESTAMP,
    CONSTRAINT uk_units_code UNIQUE (code),
    CONSTRAINT fk_units_property FOREIGN KEY (property_id) REFERENCES properties (id) ON DELETE CASCADE
);
CREATE INDEX idx_units_code     ON units (code);
CREATE INDEX idx_units_property ON units (property_id);
CREATE INDEX idx_units_status   ON units (status);

-- Amenities normalized out of units (2NF - no repeating groups / multivalued column)
CREATE TABLE unit_amenities (
    unit_id BIGINT      NOT NULL,
    amenity VARCHAR(80) NOT NULL,
    CONSTRAINT pk_unit_amenities PRIMARY KEY (unit_id, amenity),
    CONSTRAINT fk_unit_amenities_unit FOREIGN KEY (unit_id) REFERENCES units (id) ON DELETE CASCADE
);
