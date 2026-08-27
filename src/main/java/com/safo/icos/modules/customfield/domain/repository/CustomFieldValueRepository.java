package com.safo.icos.modules.customfield.domain.repository;

import com.safo.icos.modules.customfield.domain.model.CustomFieldValue;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing stored custom field values.
 */
public interface CustomFieldValueRepository {
    /** Persist or update a single custom field value. */
    CustomFieldValue save(CustomFieldValue value);

    /** Persist or update multiple custom field values in batch. */
    List<CustomFieldValue> saveAll(List<CustomFieldValue> values);

    /** Find a custom field value by its ID. */
    Optional<CustomFieldValue> findById(UUID id);

    /** Retrieve all custom field values for a specific entity record. */
    List<CustomFieldValue> findAllByEntityTypeAndEntityId(EntityType entityType, UUID entityId);

    /** Delete a specific custom field value by ID. */
    void deleteById(UUID id);

    /** Delete all custom field values associated with an entity record. */
    void deleteByEntityTypeAndEntityId(EntityType entityType, UUID entityId);
}

