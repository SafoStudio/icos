package com.safo.icos.modules.customfield.domain.repository;

import com.safo.icos.modules.customfield.domain.model.CustomFieldValue;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import java.util.List;
import java.util.UUID;

public interface CustomFieldValueRepository {
    CustomFieldValue save(CustomFieldValue value);
    List<CustomFieldValue> findAllByEntityTypeAndEntityId(EntityType entityType, UUID entityId);
}
