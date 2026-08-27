-- Extend Custom Field Definitions Schema with validation, conditional display, and UI metadata

ALTER TABLE custom_field_definitions ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE custom_field_definitions ADD COLUMN IF NOT EXISTS placeholder VARCHAR(255);
ALTER TABLE custom_field_definitions ADD COLUMN IF NOT EXISTS default_value TEXT;
ALTER TABLE custom_field_definitions ADD COLUMN IF NOT EXISTS validation_regex VARCHAR(255);
ALTER TABLE custom_field_definitions ADD COLUMN IF NOT EXISTS validation_message VARCHAR(255);
ALTER TABLE custom_field_definitions ADD COLUMN IF NOT EXISTS min_value NUMERIC;
ALTER TABLE custom_field_definitions ADD COLUMN IF NOT EXISTS max_value NUMERIC;
ALTER TABLE custom_field_definitions ADD COLUMN IF NOT EXISTS conditional_field_key VARCHAR(100);
ALTER TABLE custom_field_definitions ADD COLUMN IF NOT EXISTS conditional_value TEXT;
ALTER TABLE custom_field_definitions ADD COLUMN IF NOT EXISTS order_index INT NOT NULL DEFAULT 0;
