package com.safo.icos.modules.customfield.application;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.customfield.application.dto.CustomFieldDefinitionRequest;
import com.safo.icos.modules.customfield.application.dto.CustomFieldValueRequest;
import com.safo.icos.modules.customfield.domain.model.CustomFieldDefinition;
import com.safo.icos.modules.customfield.domain.model.CustomFieldValue;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import com.safo.icos.modules.customfield.domain.model.enums.FieldType;
import com.safo.icos.modules.customfield.domain.repository.CustomFieldDefinitionRepository;
import com.safo.icos.modules.customfield.domain.repository.CustomFieldValueRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for dynamic custom field management across system entities.
 * <p>
 * Handles the complete lifecycle of custom field definitions (creation, modification,
 * validation schema definitions, deletion) and manages value persistence with type-safe
 * mapping and validation rules (regular expressions, numerical ranges, enum choices, required checks).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomFieldService {

  private final CustomFieldDefinitionRepository definitionRepository;
  private final CustomFieldValueRepository valueRepository;

  /**
   * Creates a new custom field definition for the specified entity type.
   *
   * @param request Payload containing field configuration, validation rules, and options
   * @return The persisted CustomFieldDefinition
   * @throws IllegalArgumentException If key already exists for the entity type or key is invalid
   */
  @Transactional
  public CustomFieldDefinition createDefinition(CustomFieldDefinitionRequest request) {
    if (request.getKey() == null || request.getKey().trim().isEmpty()) {
      throw new IllegalArgumentException("Field key must not be blank");
    }
    String normalizedKey = request.getKey().trim().toLowerCase().replaceAll("[^a-z0-9_]", "_");

    definitionRepository.findByEntityTypeAndKey(request.getEntityType(), normalizedKey)
            .ifPresent(existing -> {
              throw new IllegalArgumentException("Field with key '" + normalizedKey + "' already exists for " + request.getEntityType());
            });

    CustomFieldDefinition definition = CustomFieldDefinition.builder()
            .entityType(request.getEntityType())
            .key(normalizedKey)
            .label(request.getLabel() != null ? request.getLabel().trim() : normalizedKey)
            .fieldType(request.getFieldType())
            .refEntityType(request.getRefEntityType())
            .required(request.isRequired())
            .filterable(request.isFilterable())
            .options(request.getOptions())
            .description(request.getDescription())
            .placeholder(request.getPlaceholder())
            .defaultValue(request.getDefaultValue())
            .validationRegex(request.getValidationRegex())
            .validationMessage(request.getValidationMessage())
            .minValue(request.getMinValue())
            .maxValue(request.getMaxValue())
            .conditionalFieldKey(request.getConditionalFieldKey())
            .conditionalValue(request.getConditionalValue())
            .orderIndex(request.getOrderIndex())
            .build();

    log.info("Created custom field definition '{}' [{}] for entity {}", definition.getLabel(), definition.getKey(), definition.getEntityType());
    return definitionRepository.save(definition);
  }

  /**
   * Updates an existing custom field definition.
   * <p>
   * Only fields that are explicitly provided (non-null) in the request will be updated.
   * Boolean flags {@code required} and {@code filterable} are always updated because they are primitive types.
   * The {@code orderIndex} is also updated unconditionally (primitive int).
   *
   * @param id Identifier of the definition to update
   * @param request Updated field attributes
   * @return The updated CustomFieldDefinition
   * @throws NotFoundException If the definition does not exist
   */
  @Transactional
  public CustomFieldDefinition updateDefinition(UUID id, CustomFieldDefinitionRequest request) {
    CustomFieldDefinition definition = definitionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Custom field definition not found for id: " + id));

    if (request.getLabel() != null && !request.getLabel().trim().isEmpty()) {
      definition.setLabel(request.getLabel().trim());
    }
    if (request.getFieldType() != null) {
      definition.setFieldType(request.getFieldType());
    }
    // Primitive types – always set
    definition.setRequired(request.isRequired());
    definition.setFilterable(request.isFilterable());
    definition.setOrderIndex(request.getOrderIndex());

    // Reference types – update only if provided
    if (request.getOptions() != null) {
      definition.setOptions(request.getOptions());
    }
    if (request.getDescription() != null) {
      definition.setDescription(request.getDescription());
    }
    if (request.getPlaceholder() != null) {
      definition.setPlaceholder(request.getPlaceholder());
    }
    if (request.getDefaultValue() != null) {
      definition.setDefaultValue(request.getDefaultValue());
    }
    if (request.getValidationRegex() != null) {
      definition.setValidationRegex(request.getValidationRegex());
    }
    if (request.getValidationMessage() != null) {
      definition.setValidationMessage(request.getValidationMessage());
    }
    if (request.getMinValue() != null) {
      definition.setMinValue(request.getMinValue());
    }
    if (request.getMaxValue() != null) {
      definition.setMaxValue(request.getMaxValue());
    }
    if (request.getConditionalFieldKey() != null) {
      definition.setConditionalFieldKey(request.getConditionalFieldKey());
    }
    if (request.getConditionalValue() != null) {
      definition.setConditionalValue(request.getConditionalValue());
    }
    if (request.getRefEntityType() != null) {
      definition.setRefEntityType(request.getRefEntityType());
    }

    definition.setUpdatedAt(LocalDateTime.now());

    log.info("Updated custom field definition id: {}", id);
    return definitionRepository.save(definition);
  }

  /**
   * Deletes a custom field definition and any associated stored values.
   *
   * @param id Identifier of the definition to delete
   */
  @Transactional
  public void deleteDefinition(UUID id) {
    if (!definitionRepository.existsById(id)) {
      throw new NotFoundException("Custom field definition not found for id: " + id);
    }
    definitionRepository.deleteById(id);
    log.info("Deleted custom field definition id: {}", id);
  }

  /**
   * Retrieves a single definition by its ID.
   *
   * @param id Identifier of the definition
   * @return The CustomFieldDefinition
   */
  public CustomFieldDefinition getDefinition(UUID id) {
    return definitionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Custom field definition not found for id: " + id));
  }

  /**
   * Retrieves all custom field definitions for a specific entity type, or all definitions if null.
   *
   * @param entityType Optional target entity type
   * @return List of custom field definitions ordered by sort index and creation date
   */
  public List<CustomFieldDefinition> getDefinitions(EntityType entityType) {
    if (entityType == null) {
      return definitionRepository.findAll();
    }
    return definitionRepository.findAllByEntityType(entityType);
  }

  /**
   * Retrieves all custom field definitions in the system.
   *
   * @return List of all custom field definitions
   */
  public List<CustomFieldDefinition> getAllDefinitions() {
    return definitionRepository.findAll();
  }

  /**
   * Retrieves all populated custom field values for a specific entity instance.
   *
   * @param entityType Target entity type
   * @param entityId Target entity record UUID
   * @return List of stored custom field values with attached definition metadata
   */
  public List<CustomFieldValue> getValues(EntityType entityType, UUID entityId) {
    return valueRepository.findAllByEntityTypeAndEntityId(entityType, entityId);
  }

  /**
   * Sets or updates a single custom field value for an entity instance with validation.
   * This method is transactional; it runs within its own transaction when called externally.
   *
   * @param request Payload containing entity type, entity ID, field key, and new value
   * @return The persisted CustomFieldValue
   */
  @Transactional
  public CustomFieldValue setValue(CustomFieldValueRequest request) {
    return doSetValue(request);
  }

  /**
   * Sets multiple custom field values for an entity instance in a single transaction.
   * All operations run within one transaction; any failure rolls back the entire batch.
   *
   * @param requests List of field value requests
   * @return List of persisted CustomFieldValue entities
   */
  @Transactional
  public List<CustomFieldValue> setValues(List<CustomFieldValueRequest> requests) {
    List<CustomFieldValue> results = new ArrayList<>();
    for (CustomFieldValueRequest req : requests) {
      results.add(doSetValue(req)); // Uses the same transaction as the outer method
    }
    return results;
  }

  /**
   * Deletes a custom field value by its ID.
   *
   * @param id Value UUID
   */
  @Transactional
  public void deleteValue(UUID id) {
    valueRepository.deleteById(id);
  }

  // ----------------------------- PRIVATE HELPERS -----------------------------

  /**
   * Internal core logic for setting a value. Used by both {@link #setValue} and {@link #setValues}.
   */
  private CustomFieldValue doSetValue(CustomFieldValueRequest request) {
    CustomFieldDefinition definition = definitionRepository
            .findByEntityTypeAndKey(request.getEntityType(), request.getFieldKey())
            .orElseThrow(() -> new NotFoundException(
                    "Custom field definition not found for key: " + request.getFieldKey() + " in " + request.getEntityType()));

    validateValue(request.getValue(), definition);

    CustomFieldValue value = valueRepository
            .findAllByEntityTypeAndEntityId(request.getEntityType(), request.getEntityId())
            .stream()
            .filter(v -> v.getDefinition().getId().equals(definition.getId()))
            .findFirst()
            .orElseGet(() -> CustomFieldValue.builder()
                    .entityType(request.getEntityType())
                    .entityId(request.getEntityId())
                    .definition(definition)
                    .build());

    mapValue(value, request.getValue(), definition);
    value.setUpdatedAt(LocalDateTime.now());

    return valueRepository.save(value);
  }

  /**
   * Validates user input against the field definition's constraints (required, enum options, regex, numerical range).
   */
  private void validateValue(Object value, CustomFieldDefinition definition) {
    boolean isBlankOrNull = isBlankOrNull(value);

    validateRequired(definition, isBlankOrNull);

    if (isBlankOrNull) {
      return;
    }

    String stringVal = value.toString().trim();

    validateEnumChoice(stringVal, definition);
    validateRegex(stringVal, definition);
    validateNumberRange(stringVal, definition);
  }

  /**
   * Checks if the value is null or blank.
   */
  private boolean isBlankOrNull(Object value) {
    return value == null || value.toString().trim().isEmpty();
  }

  /**
   * Validates that the field is not null/blank when required.
   */
  private void validateRequired(CustomFieldDefinition definition, boolean isBlankOrNull) {
    if (definition.isRequired() && isBlankOrNull) {
      throw new IllegalArgumentException("Field '" + definition.getLabel() + "' is required");
    }
  }

  /**
   * Validates that the value is among allowed options for ENUM fields.
   */
  private void validateEnumChoice(String stringVal, CustomFieldDefinition definition) {
    if (definition.getFieldType() == FieldType.ENUM
            && definition.getOptions() != null
            && !definition.getOptions().contains(stringVal)) {
      throw new IllegalArgumentException(
              "Invalid choice for field '" + definition.getLabel() + "': " + stringVal +
                      ". Allowed: " + definition.getOptions());
    }
  }

  /**
   * Validates the value against a regex pattern if defined.
   * If the pattern itself is invalid (PatternSyntaxException), an IllegalArgumentException is thrown.
   */
  private void validateRegex(String stringVal, CustomFieldDefinition definition) {
    if (definition.getValidationRegex() == null || definition.getValidationRegex().isBlank()) {
      return;
    }

    try {
      Pattern pattern = Pattern.compile(definition.getValidationRegex());
      if (!pattern.matcher(stringVal).matches()) {
        String errMsg = (definition.getValidationMessage() != null && !definition.getValidationMessage().isBlank())
                ? definition.getValidationMessage()
                : "Invalid format for field '" + definition.getLabel() + "'";
        throw new IllegalArgumentException(errMsg);
      }
    } catch (PatternSyntaxException e) {
      log.error("Invalid regex pattern defined on custom field {}: {}", definition.getKey(), definition.getValidationRegex(), e);
      throw new IllegalArgumentException("Invalid validation pattern for field '" + definition.getLabel() + "'");
    }
  }

  /**
   * Validates that a numeric value is within the defined min/max range.
   */
  private void validateNumberRange(String stringVal, CustomFieldDefinition definition) {
    if (definition.getFieldType() != FieldType.NUMBER) {
      return;
    }

    try {
      BigDecimal numVal = new BigDecimal(stringVal);

      if (definition.getMinValue() != null && numVal.compareTo(definition.getMinValue()) < 0) {
        throw new IllegalArgumentException("Field '" + definition.getLabel() +
                "' must be at least " + definition.getMinValue());
      }

      if (definition.getMaxValue() != null && numVal.compareTo(definition.getMaxValue()) > 0) {
        throw new IllegalArgumentException("Field '" + definition.getLabel() +
                "' cannot exceed " + definition.getMaxValue());
      }

    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid number format for field '" +
              definition.getLabel() + "': " + stringVal);
    }
  }

  /**
   * Maps an untyped object into the typed storage columns of CustomFieldValue.
   */
  private void mapValue(CustomFieldValue target, Object value, CustomFieldDefinition definition) {
    if (value == null || value.toString().trim().isEmpty()) {
      clearValueFields(target);
      return;
    }

    switch (definition.getFieldType()) {
      case STRING, MULTILINE, ENUM, MULTI_SELECT, URL ->
              target.setValueText(value.toString().trim());
      case NUMBER ->
              target.setValueNumber(new BigDecimal(value.toString().trim()));
      case BOOLEAN ->
              target.setValueBool(Boolean.valueOf(value.toString().trim()));
      case DATE ->
              target.setValueDate(parseDate(value));
      case ENTITY_REF -> {
        target.setValueEntityId(UUID.fromString(value.toString().trim()));
        target.setValueEntityType(definition.getRefEntityType());
      }
    }
  }

  /**
   * Clears all typed value fields on the target.
   */
  private void clearValueFields(CustomFieldValue target) {
    target.setValueText(null);
    target.setValueNumber(null);
    target.setValueBool(null);
    target.setValueDate(null);
    target.setValueEntityId(null);
    target.setValueEntityType(null);
  }

  /**
   * Parses a value into a LocalDateTime using pattern matching (Java 17+).
   */
  private LocalDateTime parseDate(Object value) {
    return switch (value) {
      case LocalDateTime ldt -> ldt;
      case LocalDate ld -> ld.atStartOfDay();
      default -> parseDateString(value.toString().trim());
    };
  }

  /**
   * Parses a date string into a LocalDateTime.
   */
  private LocalDateTime parseDateString(String dateStr) {
    try {
      if (isIsoLocalDate(dateStr)) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
      }
      return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
    } catch (DateTimeParseException e) {
      return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
  }

  /**
   * Checks if the string is in ISO local date format (yyyy-MM-dd).
   */
  private boolean isIsoLocalDate(String dateStr) {
    return dateStr.length() == 10 && dateStr.contains("-");
  }
}