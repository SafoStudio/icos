package com.safo.icos.modules.customfield.infrastructure.repository.adapter;

import com.safo.icos.modules.customfield.domain.model.CustomFieldDefinition;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import com.safo.icos.modules.customfield.domain.repository.CustomFieldDefinitionRepository;
import com.safo.icos.modules.customfield.infrastructure.repository.jpa.CustomFieldDefinitionJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomFieldDefinitionRepositoryAdapter implements CustomFieldDefinitionRepository {
    private final CustomFieldDefinitionJpaRepository jpaRepository;

    @Override
    public CustomFieldDefinition save(CustomFieldDefinition definition) {
        return jpaRepository.save(definition);
    }

    @Override
    public Optional<CustomFieldDefinition> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<CustomFieldDefinition> findAllByEntityType(EntityType entityType) {
        return jpaRepository.findAllByEntityType(entityType);
    }

    @Override
    public Optional<CustomFieldDefinition> findByEntityTypeAndKey(EntityType entityType, String key) {
        return jpaRepository.findByEntityTypeAndKey(entityType, key);
    }
}
