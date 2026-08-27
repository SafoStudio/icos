package com.safo.icos.modules.customfield.domain.model;

import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import com.safo.icos.modules.customfield.domain.model.enums.FieldType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Defines a custom field schema for a specific entity type (e.g. USER, CANDIDATE, JOB, ONBOARDING).
 * Supports rich configuration including data types, validation rules, choices, and conditional display.
 */
@Entity
@Table(name = "custom_field_definitions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldDefinition {

  @Id
  private UUID id;

  /** Target entity type to which this custom field attaches. */
  @Enumerated(EnumType.STRING)
  @Column(name = "entity_type", nullable = false)
  private EntityType entityType;

  /** Unique programmatic key (e.g., 'visa_status', 'relocation_city'). */
  @Column(name = "key", nullable = false)
  private String key;

  /** User-facing display label (e.g., 'Visa Sponsorship Needed', 'Expected Salary'). */
  @Column(name = "label", nullable = false)
  private String label;

  /** Data type of the field (STRING, MULTILINE, NUMBER, DATE, BOOLEAN, ENUM, MULTI_SELECT, URL, ENTITY_REF). */
  @Enumerated(EnumType.STRING)
  @Column(name = "field_type", nullable = false)
  private FieldType fieldType;

  /** Reference entity type name when fieldType is ENTITY_REF. */
  @Column(name = "ref_entity_type")
  private String refEntityType;

  /** Whether the field is mandatory when submitting or updating entity records. */
  @Column(name = "is_required", nullable = false)
  private boolean required;

  /** Whether the field is indexed/filterable in list views and search filters. */
  @Column(name = "is_filterable", nullable = false)
  private boolean filterable;

  /** Predefined options for ENUM (single-select) and MULTI_SELECT field types stored as JSON array. */
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "options", columnDefinition = "jsonb")
  private List<String> options;

  /** Optional detailed description or helper tooltip for form users. */
  @Column(name = "description")
  private String description;

  /** Optional input placeholder text. */
  @Column(name = "placeholder")
  private String placeholder;

  /** Optional default initial value. */
  @Column(name = "default_value")
  private String defaultValue;

  /** Optional regular expression pattern for client/server string validation. */
  @Column(name = "validation_regex")
  private String validationRegex;

  /** Custom user-facing error message displayed when validation regex fails. */
  @Column(name = "validation_message")
  private String validationMessage;

  /** Minimum value allowed for numeric fields. */
  @Column(name = "min_value")
  private BigDecimal minValue;

  /** Maximum value allowed for numeric fields. */
  @Column(name = "max_value")
  private BigDecimal maxValue;

  /** Key of the parent field on the same entity that controls conditional visibility of this field. */
  @Column(name = "conditional_field_key")
  private String conditionalFieldKey;

  /** Expected value of the parent field that makes this field visible (e.g. 'true', 'YES', 'RELOCATION'). */
  @Column(name = "conditional_value")
  private String conditionalValue;

  /** Sorting sequence index for rendering fields in forms. */
  @Builder.Default
  @Column(name = "order_index", nullable = false)
  private int orderIndex = 0;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    if (id == null) {
      id = UUID.randomUUID();
    }
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }
}