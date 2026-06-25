package com.safo.icos.modules.customfield.application;

import com.safo.icos.modules.customfield.application.dto.CustomFieldDefinitionRequest;
import com.safo.icos.modules.customfield.application.dto.CustomFieldValueRequest;
import com.safo.icos.modules.customfield.domain.model.CustomFieldDefinition;
import com.safo.icos.modules.customfield.domain.model.CustomFieldValue;
import com.safo.icos.modules.customfield.domain.model.enums.EntityType;
import com.safo.icos.modules.customfield.domain.model.enums.FieldType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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
}
