package com.safo.icos.modules.customfield.infrastructure.repository.jpa;

import com.safo.icos.modules.customfield.domain.model.CustomFieldDefinition;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomFieldDefinitionJpaRepository extends JpaRepository<CustomFieldDefinition, UUID> {
    List<CustomFieldDefinition> findAllByEntityTypeOrderByOrderIndexAscCreatedAtAsc(EntityType entityType);
    Optional<CustomFieldDefinition> findByEntityTypeAndKey(EntityType entityType, String key);
}

