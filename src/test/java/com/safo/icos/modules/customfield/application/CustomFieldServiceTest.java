package com.safo.icos.modules.customfield.application;

import com.safo.icos.modules.customfield.application.dto.CustomFieldDefinitionRequest;
import com.safo.icos.modules.customfield.application.dto.CustomFieldValueRequest;
import com.safo.icos.modules.customfield.domain.model.CustomFieldDefinition;
import com.safo.icos.modules.customfield.domain.model.CustomFieldValue;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import com.safo.icos.modules.customfield.domain.model.enums.FieldType;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CustomFieldServiceTest {

    @Autowired
    private CustomFieldService customFieldService;

    @Test
    void shouldCreateDefinitionAndSetValue() {
        // 1. Create definition
        CustomFieldDefinitionRequest defRequest = new CustomFieldDefinitionRequest();
        defRequest.setEntityType(EntityType.USER);
        defRequest.setKey("shirt_size");
        defRequest.setLabel("Shirt Size");
        defRequest.setFieldType(FieldType.STRING);
        defRequest.setRequired(false);
        defRequest.setFilterable(true);

        CustomFieldDefinition definition = customFieldService.createDefinition(defRequest);
        assertThat(definition.getId()).isNotNull();
        assertThat(definition.getKey()).isEqualTo("shirt_size");

        // 2. Set value for a user
        UUID userId = UUID.randomUUID();
        CustomFieldValueRequest valRequest = new CustomFieldValueRequest();
        valRequest.setEntityType(EntityType.USER);
        valRequest.setEntityId(userId);
        valRequest.setFieldKey("shirt_size");
        valRequest.setValue("XL");

        CustomFieldValue value = customFieldService.setValue(valRequest);
        assertThat(value.getId()).isNotNull();
        assertThat(value.getValueText()).isEqualTo("XL");

        // 3. Retrieve values
        List<CustomFieldValue> values = customFieldService.getValues(EntityType.USER, userId);
        assertThat(values).hasSize(1);
        assertThat(values.get(0).getValueText()).isEqualTo("XL");
    }

    @Test
    void shouldHandleNumberType() {
        // 1. Create numeric definition
        CustomFieldDefinitionRequest defRequest = new CustomFieldDefinitionRequest();
        defRequest.setEntityType(EntityType.USER);
        defRequest.setKey("bonus_points");
        defRequest.setLabel("Bonus Points");
        defRequest.setFieldType(FieldType.NUMBER);

        customFieldService.createDefinition(defRequest);

        // 2. Set numeric value
        UUID userId = UUID.randomUUID();
        CustomFieldValueRequest valRequest = new CustomFieldValueRequest();
        valRequest.setEntityType(EntityType.USER);
        valRequest.setEntityId(userId);
        valRequest.setFieldKey("bonus_points");
        valRequest.setValue("150.50");

        CustomFieldValue value = customFieldService.setValue(valRequest);
        assertThat(value.getValueNumber()).isEqualByComparingTo("150.50");
    }

    @Test
    void shouldHandleCandidateCustomFieldWithRegexValidation() {
        // 1. Create Candidate portfolio URL field with regex validation
        CustomFieldDefinitionRequest defRequest = new CustomFieldDefinitionRequest();
        defRequest.setEntityType(EntityType.CANDIDATE);
        defRequest.setKey("portfolio_url");
        defRequest.setLabel("Portfolio URL");
        defRequest.setFieldType(FieldType.URL);
        defRequest.setValidationRegex("^https?://.+");
        defRequest.setValidationMessage("Must be a valid HTTP or HTTPS URL");

        CustomFieldDefinition def = customFieldService.createDefinition(defRequest);
        assertThat(def.getId()).isNotNull();

        UUID candidateId = UUID.randomUUID();

        // 2. Invalid URL should throw IllegalArgumentException
        CustomFieldValueRequest invalidReq = new CustomFieldValueRequest();
        invalidReq.setEntityType(EntityType.CANDIDATE);
        invalidReq.setEntityId(candidateId);
        invalidReq.setFieldKey("portfolio_url");
        invalidReq.setValue("ftp://bad-link");

        assertThrows(IllegalArgumentException.class, () -> customFieldService.setValue(invalidReq));

        // 3. Valid URL should succeed
        CustomFieldValueRequest validReq = new CustomFieldValueRequest();
        validReq.setEntityType(EntityType.CANDIDATE);
        validReq.setEntityId(candidateId);
        validReq.setFieldKey("portfolio_url");
        validReq.setValue("https://github.com/myprofile");

        CustomFieldValue val = customFieldService.setValue(validReq);
        assertThat(val.getValueText()).isEqualTo("https://github.com/myprofile");
    }

    @Test
    void shouldHandleBatchSettingValues() {
        // 1. Create 2 fields for Onboarding
        CustomFieldDefinitionRequest def1 = new CustomFieldDefinitionRequest();
        def1.setEntityType(EntityType.ONBOARDING);
        def1.setKey("equipment_shipped");
        def1.setLabel("Equipment Shipped");
        def1.setFieldType(FieldType.BOOLEAN);
        customFieldService.createDefinition(def1);

        CustomFieldDefinitionRequest def2 = new CustomFieldDefinitionRequest();
        def2.setEntityType(EntityType.ONBOARDING);
        def2.setKey("mentor_notes");
        def2.setLabel("Mentor Notes");
        def2.setFieldType(FieldType.MULTILINE);
        customFieldService.createDefinition(def2);

        // 2. Batch set values
        UUID processId = UUID.randomUUID();
        CustomFieldValueRequest val1 = new CustomFieldValueRequest();
        val1.setEntityType(EntityType.ONBOARDING);
        val1.setEntityId(processId);
        val1.setFieldKey("equipment_shipped");
        val1.setValue(true);

        CustomFieldValueRequest val2 = new CustomFieldValueRequest();
        val2.setEntityType(EntityType.ONBOARDING);
        val2.setEntityId(processId);
        val2.setFieldKey("mentor_notes");
        val2.setValue("Candidate assigned laptop and welcome kit.");

        List<CustomFieldValue> saved = customFieldService.setValues(List.of(val1, val2));
        assertThat(saved).hasSize(2);

        List<CustomFieldValue> retrieved = customFieldService.getValues(EntityType.ONBOARDING, processId);
        assertThat(retrieved).hasSize(2);
    }
}

