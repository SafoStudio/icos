package com.safo.icos.modules.customfield.application.dto;

import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import com.safo.icos.modules.customfield.domain.model.enums.FieldType;
import java.util.List;
import lombok.Data;

/**
 * Request payload for creating a custom field definition.
 */
@Data
public class CustomFieldDefinitionRequest {
  private EntityType entityType;
  private String key;
  private String label;
  private FieldType fieldType;
  private String refEntityType;
  private boolean required;
  private boolean filterable;
  private List<String> options;
}