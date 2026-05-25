package com.safo.icos.modules.customfield.domain.repository;

import com.safo.icos.modules.customfield.domain.model.CustomFieldDefinition;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomFieldDefinitionRepository {
    CustomFieldDefinition save(CustomFieldDefinition definition);
    Optional<CustomFieldDefinition> findById(UUID id);
    List<CustomFieldDefinition> findAllByEntityType(EntityType entityType);
    Optional<CustomFieldDefinition> findByEntityTypeAndKey(EntityType entityType, String key);
}
