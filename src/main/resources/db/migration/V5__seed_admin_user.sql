-- =========================================================
-- V5: Seed default administrator
-- Email   : admin@pmfms.com
-- Password: Admin@123   (bcrypt) - CHANGE IMMEDIATELY IN PRODUCTION
-- =========================================================
INSERT INTO users (full_name, email, password, phone, role, active, created_at)
VALUES ('System Administrator',
        'admin@pmfms.com',
        '$2b$10$E1dWrNc6qoYE8iO0gmQWPeCzG7tEAwxzpa83kpWGQaYIScMfs7jwe',
        NULL,
        'ADMIN',
        TRUE,
        CURRENT_TIMESTAMP);
