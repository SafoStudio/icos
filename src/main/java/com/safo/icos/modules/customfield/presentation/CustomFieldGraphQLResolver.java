package com.safo.icos.modules.customfield.presentation;

import com.safo.icos.modules.customfield.application.CustomFieldService;
import com.safo.icos.modules.customfield.application.dto.CustomFieldDefinitionRequest;
import com.safo.icos.modules.customfield.application.dto.CustomFieldValueRequest;
import com.safo.icos.modules.customfield.domain.model.CustomFieldDefinition;
import com.safo.icos.modules.customfield.domain.model.CustomFieldValue;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CustomFieldGraphQLResolver {

    private final CustomFieldService customFieldService;

    @QueryMapping
    public List<CustomFieldDefinition> customFieldDefinitions(@Argument EntityType entityType) {
        return customFieldService.getDefinitions(entityType);
    }

    @QueryMapping
    public List<CustomFieldValue> customFieldValues(@Argument EntityType entityType, @Argument UUID entityId) {
        return customFieldService.getValues(entityType, entityId);
    }

    @MutationMapping
    public CustomFieldDefinition createCustomFieldDefinition(@Argument CustomFieldDefinitionRequest input) {
        return customFieldService.createDefinition(input);
    }

    @MutationMapping
    public CustomFieldValue setCustomFieldValue(@Argument CustomFieldValueRequest input) {
        return customFieldService.setValue(input);
    }
}
