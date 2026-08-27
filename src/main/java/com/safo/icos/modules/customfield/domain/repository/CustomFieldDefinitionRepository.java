package com.safo.icos.modules.customfield.domain.repository;

import com.safo.icos.modules.customfield.domain.model.CustomFieldDefinition;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing custom field definitions.
 */
public interface CustomFieldDefinitionRepository {
    /** Persist or update a custom field definition. */
    CustomFieldDefinition save(CustomFieldDefinition definition);

    /** Find definition by unique identifier. */
    Optional<CustomFieldDefinition> findById(UUID id);

    /** Find all definitions in the system. */
    List<CustomFieldDefinition> findAll();

    /** Find all definitions configured for a specific entity type. */
    List<CustomFieldDefinition> findAllByEntityType(EntityType entityType);

    /** Find definition by target entity type and field key. */
    Optional<CustomFieldDefinition> findByEntityTypeAndKey(EntityType entityType, String key);

    /** Delete definition by unique identifier. */
    void deleteById(UUID id);

    /** Check if definition exists by ID. */
    boolean existsById(UUID id);
}

