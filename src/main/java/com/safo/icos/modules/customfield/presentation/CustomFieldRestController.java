package com.safo.icos.modules.customfield.presentation;

import com.safo.icos.modules.customfield.application.CustomFieldService;
import com.safo.icos.modules.customfield.application.dto.CustomFieldDefinitionRequest;
import com.safo.icos.modules.customfield.application.dto.CustomFieldValueRequest;
import com.safo.icos.modules.customfield.domain.model.CustomFieldDefinition;
import com.safo.icos.modules.customfield.domain.model.CustomFieldValue;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import com.safo.icos.security.permission.RequirePermission;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API controller for dynamic custom fields management.
 * Provides endpoints for managing definitions and retrieving/setting values.
 */
@RestController
@RequestMapping("/api/v1/custom-fields")
@RequiredArgsConstructor
public class CustomFieldRestController {

  private final CustomFieldService customFieldService;

  /**
   * List custom field definitions, optionally filtered by entity type.
   */
  @GetMapping("/definitions")
  @RequirePermission("customfield:read")
  public ResponseEntity<List<CustomFieldDefinition>> getDefinitions(
      @RequestParam(required = false) EntityType entityType) {
    return ResponseEntity.ok(customFieldService.getDefinitions(entityType));
  }

  /**
   * Get a single custom field definition by UUID.
   */
  @GetMapping("/definitions/{id}")
  @RequirePermission("customfield:read")
  public ResponseEntity<CustomFieldDefinition> getDefinition(@PathVariable UUID id) {
    return ResponseEntity.ok(customFieldService.getDefinition(id));
  }

  /**
   * Create a new custom field definition.
   */
  @PostMapping("/definitions")
  @RequirePermission("customfield:manage")
  public ResponseEntity<CustomFieldDefinition> createDefinition(
      @RequestBody CustomFieldDefinitionRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(customFieldService.createDefinition(request));
  }

  /**
   * Update an existing custom field definition.
   */
  @PutMapping("/definitions/{id}")
  @RequirePermission("customfield:manage")
  public ResponseEntity<CustomFieldDefinition> updateDefinition(
      @PathVariable UUID id,
      @RequestBody CustomFieldDefinitionRequest request) {
    return ResponseEntity.ok(customFieldService.updateDefinition(id, request));
  }

  /**
   * Delete a custom field definition and its values.
   */
  @DeleteMapping("/definitions/{id}")
  @RequirePermission("customfield:manage")
  public ResponseEntity<Void> deleteDefinition(@PathVariable UUID id) {
    customFieldService.deleteDefinition(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Get custom field values for a specific entity instance.
   */
  @GetMapping("/values")
  @RequirePermission("customfield:read")
  public ResponseEntity<List<CustomFieldValue>> getValues(
      @RequestParam EntityType entityType,
      @RequestParam UUID entityId) {
    return ResponseEntity.ok(customFieldService.getValues(entityType, entityId));
  }

  /**
   * Set or update a single custom field value.
   */
  @PostMapping("/values")
  @RequirePermission("customfield:manage")
  public ResponseEntity<CustomFieldValue> setValue(@RequestBody CustomFieldValueRequest request) {
    return ResponseEntity.ok(customFieldService.setValue(request));
  }

  /**
   * Set or update multiple custom field values in batch.
   */
  @PostMapping("/values/batch")
  @RequirePermission("customfield:manage")
  public ResponseEntity<List<CustomFieldValue>> setValues(@RequestBody List<CustomFieldValueRequest> requests) {
    return ResponseEntity.ok(customFieldService.setValues(requests));
  }

  /**
   * Delete a custom field value by its UUID.
   */
  @DeleteMapping("/values/{id}")
  @RequirePermission("customfield:manage")
  public ResponseEntity<Void> deleteValue(@PathVariable UUID id) {
    customFieldService.deleteValue(id);
    return ResponseEntity.noContent().build();
  }
}
