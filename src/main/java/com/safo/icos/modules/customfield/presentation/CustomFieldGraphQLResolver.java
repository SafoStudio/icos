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
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL presentation resolver for custom field schema definitions and entity values.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class CustomFieldGraphQLResolver {

    private final CustomFieldService customFieldService;

    /**
     * Retrieves definitions for a specific entity type (e.g. USER, CANDIDATE, JOB, etc.).
     */
    @QueryMapping
    @RequirePermission("customfield:read")
    public List<CustomFieldDefinition> customFieldDefinitions(@Argument EntityType entityType) {
        return customFieldService.getDefinitions(entityType);
    }

    /**
     * Retrieves all custom field definitions in the system.
     */
    @QueryMapping
    @RequirePermission("customfield:read")
    public List<CustomFieldDefinition> allCustomFieldDefinitions() {
        return customFieldService.getAllDefinitions();
    }

    /**
     * Retrieves a single custom field definition by UUID.
     */
    @QueryMapping
    @RequirePermission("customfield:read")
    public CustomFieldDefinition customFieldDefinition(@Argument UUID id) {
        return customFieldService.getDefinition(id);
    }

    /**
     * Retrieves all populated custom field values for a specific entity instance.
     */
    @QueryMapping
    @RequirePermission("customfield:read")
    public List<CustomFieldValue> customFieldValues(@Argument EntityType entityType, @Argument UUID entityId) {
        return customFieldService.getValues(entityType, entityId);
    }

    /**
     * Creates a new custom field definition schema.
     */
    @MutationMapping
    @RequirePermission("customfield:manage")
    public CustomFieldDefinition createCustomFieldDefinition(@Argument CustomFieldDefinitionRequest input) {
        return customFieldService.createDefinition(input);
    }

    /**
     * Updates an existing custom field definition schema.
     */
    @MutationMapping
    @RequirePermission("customfield:manage")
    public CustomFieldDefinition updateCustomFieldDefinition(@Argument UUID id, @Argument CustomFieldDefinitionRequest input) {
        return customFieldService.updateDefinition(id, input);
    }

    /**
     * Deletes a custom field definition schema and associated values.
     */
    @MutationMapping
    @RequirePermission("customfield:manage")
    public Boolean deleteCustomFieldDefinition(@Argument UUID id) {
        customFieldService.deleteDefinition(id);
        return true;
    }

    /**
     * Sets a single custom field value for an entity instance.
     */
    @MutationMapping
    @RequirePermission("customfield:manage")
    public CustomFieldValue setCustomFieldValue(@Argument CustomFieldValueRequest input) {
        return customFieldService.setValue(input);
    }

    /**
     * Sets multiple custom field values in batch for an entity instance.
     */
    @MutationMapping
    @RequirePermission("customfield:manage")
    public List<CustomFieldValue> setCustomFieldValues(@Argument List<CustomFieldValueRequest> inputs) {
        return customFieldService.setValues(inputs);
    }

    /**
     * Deletes a specific custom field value by its ID.
     */
    @MutationMapping
    @RequirePermission("customfield:manage")
    public Boolean deleteCustomFieldValue(@Argument UUID id) {
        customFieldService.deleteValue(id);
        return true;
    }
}

