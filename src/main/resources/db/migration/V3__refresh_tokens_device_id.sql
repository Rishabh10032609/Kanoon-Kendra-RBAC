-- Flyway V3: add device_id to refresh_tokens
ALTER TABLE refresh_tokens ADD COLUMN IF NOT EXISTS device_id VARCHAR(100);
