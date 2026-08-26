package com.safo.icos.modules.customfield.application.dto;

import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import java.util.UUID;
import lombok.Data;

/**
 * Request payload for setting a custom field value.
 */
@Data
public class CustomFieldValueRequest {
  private EntityType entityType;
  private UUID entityId;
  private String fieldKey;
  private Object value;
}