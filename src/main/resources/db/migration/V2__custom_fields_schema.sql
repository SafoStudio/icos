-- Custom Fields Schema

CREATE TABLE custom_field_definitions (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    key VARCHAR(100) NOT NULL,
    label VARCHAR(255) NOT NULL,
    field_type VARCHAR(50) NOT NULL,
    ref_entity_type VARCHAR(50),
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    is_filterable BOOLEAN NOT NULL DEFAULT TRUE,
    options JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(entity_type, key)
);

CREATE TABLE custom_field_values (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    field_id UUID NOT NULL REFERENCES custom_field_definitions(id) ON DELETE CASCADE,
    
    value_text TEXT,
    value_number NUMERIC,
    value_bool BOOLEAN,
    value_date TIMESTAMP,
    value_entity_id UUID,
    value_entity_type VARCHAR(50),
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(entity_type, entity_id, field_id)
);

-- Triggers for updated_at
CREATE TRIGGER update_custom_field_definitions_updated_at
BEFORE UPDATE ON custom_field_definitions
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_custom_field_values_updated_at
BEFORE UPDATE ON custom_field_values
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Indexes for performance
CREATE INDEX idx_cfv_entity ON custom_field_values(entity_type, entity_id);
CREATE INDEX idx_cfv_field_text ON custom_field_values(field_id, value_text) WHERE value_text IS NOT NULL;
CREATE INDEX idx_cfv_field_number ON custom_field_values(field_id, value_number) WHERE value_number IS NOT NULL;
CREATE INDEX idx_cfv_field_date ON custom_field_values(field_id, value_date) WHERE value_date IS NOT NULL;
CREATE INDEX idx_cfv_entity_ref ON custom_field_values(value_entity_type, value_entity_id) WHERE value_entity_id IS NOT NULL;
