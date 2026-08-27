package com.safo.icos.modules.customfield.application.dto;

import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import com.safo.icos.modules.customfield.domain.model.enums.FieldType;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * Request payload for creating or updating a custom field definition schema.
 */
@Data
public class CustomFieldDefinitionRequest {
  /** Target entity type (USER, CANDIDATE, JOB, ONBOARDING, DEPARTMENT, POSITION, COMPANY). */
  private EntityType entityType;

  /** Unique programmatic key identifier. */
  private String key;

  /** Display label shown in user interfaces. */
  private String label;

  /** Data type of the field. */
  private FieldType fieldType;

  /** Entity type name if fieldType is ENTITY_REF. */
  private String refEntityType;

  /** Whether the field is mandatory. */
  private boolean required;

  /** Whether the field is included in filterable search attributes. */
  private boolean filterable = true;

  /** List of predefined choices for ENUM and MULTI_SELECT fields. */
  private List<String> options;

  /** Optional detailed explanation or tooltip for users. */
  private String description;

  /** Optional input placeholder text. */
  private String placeholder;

  /** Optional initial default value. */
  private String defaultValue;

  /** Regular expression pattern for input validation (e.g. email, phone, URL format). */
  private String validationRegex;

  /** User error message to show when validation regex fails. */
  private String validationMessage;

  /** Minimum allowed value for numeric fields. */
  private BigDecimal minValue;

  /** Maximum allowed value for numeric fields. */
  private BigDecimal maxValue;

  /** Key of the parent field controlling conditional visibility. */
  private String conditionalFieldKey;

  /** Value of parent field that activates this field's visibility. */
  private String conditionalValue;

  /** Display ordering sequence index. */
  private int orderIndex;
}