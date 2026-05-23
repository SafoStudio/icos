package com.safo.icos.modules.customfield.application.dto;

import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import lombok.Data;

import java.util.UUID;

@Data
public class CustomFieldValueRequest {
    private EntityType entityType;
    private UUID entityId;
    private String fieldKey;
    private Object value; // Dynamic value handling
}
