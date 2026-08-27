package com.safo.icos.modules.customfield.infrastructure.repository.jpa;

import com.safo.icos.modules.customfield.domain.model.CustomFieldValue;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomFieldValueJpaRepository extends JpaRepository<CustomFieldValue, UUID> {
    List<CustomFieldValue> findAllByEntityTypeAndEntityId(EntityType entityType, UUID entityId);
    void deleteByEntityTypeAndEntityId(EntityType entityType, UUID entityId);
}

