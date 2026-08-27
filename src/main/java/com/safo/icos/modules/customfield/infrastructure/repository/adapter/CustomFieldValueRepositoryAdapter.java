package com.safo.icos.modules.customfield.infrastructure.repository.adapter;

import com.safo.icos.modules.customfield.domain.model.CustomFieldValue;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import com.safo.icos.modules.customfield.domain.repository.CustomFieldValueRepository;
import com.safo.icos.modules.customfield.infrastructure.repository.jpa.CustomFieldValueJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomFieldValueRepositoryAdapter implements CustomFieldValueRepository {
    private final CustomFieldValueJpaRepository jpaRepository;

    @Override
    public CustomFieldValue save(CustomFieldValue value) {
        return jpaRepository.save(value);
    }

    @Override
    public List<CustomFieldValue> saveAll(List<CustomFieldValue> values) {
        return jpaRepository.saveAll(values);
    }

    @Override
    public Optional<CustomFieldValue> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<CustomFieldValue> findAllByEntityTypeAndEntityId(EntityType entityType, UUID entityId) {
        return jpaRepository.findAllByEntityTypeAndEntityId(entityType, entityId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteByEntityTypeAndEntityId(EntityType entityType, UUID entityId) {
        jpaRepository.deleteByEntityTypeAndEntityId(entityType, entityId);
    }
}

