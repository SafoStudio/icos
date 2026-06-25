package com.safo.icos.modules.customfield.application;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.customfield.application.dto.CustomFieldDefinitionRequest;
import com.safo.icos.modules.customfield.application.dto.CustomFieldValueRequest;
import com.safo.icos.modules.customfield.domain.model.CustomFieldDefinition;
import com.safo.icos.modules.customfield.domain.model.CustomFieldValue;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import com.safo.icos.modules.customfield.domain.repository.CustomFieldDefinitionRepository;
import com.safo.icos.modules.customfield.domain.repository.CustomFieldValueRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomFieldService {

    private final CustomFieldDefinitionRepository definitionRepository;
    private final CustomFieldValueRepository valueRepository;

    @Transactional
    public CustomFieldDefinition createDefinition(CustomFieldDefinitionRequest request) {
        CustomFieldDefinition definition = CustomFieldDefinition.builder()
                .entityType(request.getEntityType())
                .key(request.getKey())
                .label(request.getLabel())
                .fieldType(request.getFieldType())
                .refEntityType(request.getRefEntityType())
                .required(request.isRequired())
                .filterable(request.isFilterable())
                .options(request.getOptions())
                .build();
        return definitionRepository.save(definition);
    }

    public List<CustomFieldDefinition> getDefinitions(EntityType entityType) {
        return definitionRepository.findAllByEntityType(entityType);
    }

    public List<CustomFieldValue> getValues(EntityType entityType, UUID entityId) {
        return valueRepository.findAllByEntityTypeAndEntityId(entityType, entityId);
    }

    @Transactional
    public CustomFieldValue setValue(CustomFieldValueRequest request) {
        CustomFieldDefinition definition = definitionRepository.findByEntityTypeAndKey(request.getEntityType(), request.getFieldKey())
                .orElseThrow(() -> new NotFoundException("Custom field definition not found for key: " + request.getFieldKey()));

        if (definition.isRequired() && (request.getValue() == null || request.getValue().toString().isBlank())) {
            throw new IllegalArgumentException("Field " + definition.getLabel() + " is required");
        }

        if (definition.getFieldType() == com.safo.icos.modules.customfield.domain.model.enums.FieldType.ENUM 
            && request.getValue() != null 
            && definition.getOptions() != null 
            && !definition.getOptions().contains(request.getValue().toString())) {
            throw new IllegalArgumentException("Invalid value for field " + definition.getLabel() + ". Allowed: " + definition.getOptions());
        }

        // Try to find existing value to update
        CustomFieldValue value = valueRepository.findAllByEntityTypeAndEntityId(request.getEntityType(), request.getEntityId())
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

    private void mapValue(CustomFieldValue target, Object value, CustomFieldDefinition definition) {
        if (value == null) {
            target.setValueText(null);
            target.setValueNumber(null);
            target.setValueBool(null);
            target.setValueDate(null);
            target.setValueEntityId(null);
            target.setValueEntityType(null);
            return;
        }

        switch (definition.getFieldType()) {
            case STRING, ENUM -> target.setValueText(value.toString());
            case NUMBER -> target.setValueNumber(new BigDecimal(value.toString()));
            case BOOLEAN -> target.setValueBool(Boolean.valueOf(value.toString()));
            case DATE -> {
                if (value instanceof LocalDateTime ldt) {
                    target.setValueDate(ldt);
                } else {
                    target.setValueDate(LocalDateTime.parse(value.toString(), DateTimeFormatter.ISO_DATE_TIME));
                }
            }
            case ENTITY_REF -> {
                target.setValueEntityId(UUID.fromString(value.toString()));
                target.setValueEntityType(definition.getRefEntityType());
            }
        }
    }
}
