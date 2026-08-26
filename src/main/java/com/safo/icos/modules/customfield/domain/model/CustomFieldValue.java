package com.safo.icos.modules.customfield.domain.model;

import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Stores a custom field value for a specific entity.
 * The field type determines which value column is populated.
 */
@Entity
@Table(name = "custom_field_values")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldValue {

  @Id
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "entity_type", nullable = false)
  private EntityType entityType;

  @Column(name = "entity_id", nullable = false)
  private UUID entityId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "field_id", nullable = false)
  private CustomFieldDefinition definition;

  @Column(name = "value_text")
  private String valueText;

  @Column(name = "value_number")
  private BigDecimal valueNumber;

  @Column(name = "value_bool")
  private Boolean valueBool;

  @Column(name = "value_date")
  private LocalDateTime valueDate;

  @Column(name = "value_entity_id")
  private UUID valueEntityId;

  @Column(name = "value_entity_type")
  private String valueEntityType;

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