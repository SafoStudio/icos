import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import {
  CustomFieldDefinition,
  CustomFieldValue,
  CustomFieldDefinitionInput,
  CustomFieldValueInput,
  EntityType
} from '@models';

@Injectable({
  providedIn: 'root'
})
export class CustomFieldsService {
  private readonly api = inject(ApiService);

  getDefinitions(entityType?: EntityType): Observable<CustomFieldDefinition[]> {
    const query = `
      query GetCustomFieldDefinitions($entityType: EntityType) {
        customFieldDefinitions(entityType: $entityType) {
          id
          entityType
          key
          label
          fieldType
          refEntityType
          required
          filterable
          options
          description
          placeholder
          defaultValue
          validationRegex
          validationMessage
          minValue
          maxValue
          conditionalFieldKey
          conditionalValue
          orderIndex
          createdAt
          updatedAt
        }
      }
    `;

    return this.api
      .graphql<{ customFieldDefinitions: CustomFieldDefinition[] }>(query, { entityType: entityType || null })
      .pipe(map((res) => res.customFieldDefinitions));
  }

  getAllDefinitions(): Observable<CustomFieldDefinition[]> {
    const query = `
      query GetAllCustomFieldDefinitions {
        allCustomFieldDefinitions {
          id
          entityType
          key
          label
          fieldType
          refEntityType
          required
          filterable
          options
          description
          placeholder
          defaultValue
          validationRegex
          validationMessage
          minValue
          maxValue
          conditionalFieldKey
          conditionalValue
          orderIndex
          createdAt
          updatedAt
        }
      }
    `;

    return this.api
      .graphql<{ allCustomFieldDefinitions: CustomFieldDefinition[] }>(query)
      .pipe(map((res) => res.allCustomFieldDefinitions));
  }

  getDefinition(id: string): Observable<CustomFieldDefinition> {
    const query = `
      query GetCustomFieldDefinition($id: UUID!) {
        customFieldDefinition(id: $id) {
          id
          entityType
          key
          label
          fieldType
          refEntityType
          required
          filterable
          options
          description
          placeholder
          defaultValue
          validationRegex
          validationMessage
          minValue
          maxValue
          conditionalFieldKey
          conditionalValue
          orderIndex
          createdAt
          updatedAt
        }
      }
    `;

    return this.api
      .graphql<{ customFieldDefinition: CustomFieldDefinition }>(query, { id })
      .pipe(map((res) => res.customFieldDefinition));
  }

  getValues(entityType: EntityType, entityId: string): Observable<CustomFieldValue[]> {
    const query = `
      query GetCustomFieldValues($entityType: EntityType!, $entityId: UUID!) {
        customFieldValues(entityType: $entityType, entityId: $entityId) {
          id
          entityType
          entityId
          valueText
          valueNumber
          valueBool
          valueDate
          valueEntityId
          valueEntityType
          definition {
            id
            entityType
            key
            label
            fieldType
            refEntityType
            required
            filterable
            options
            description
            placeholder
            defaultValue
            validationRegex
            validationMessage
            minValue
            maxValue
            conditionalFieldKey
            conditionalValue
            orderIndex
          }
        }
      }
    `;

    return this.api
      .graphql<{ customFieldValues: CustomFieldValue[] }>(query, { entityType, entityId })
      .pipe(map((res) => res.customFieldValues));
  }

  createDefinition(input: CustomFieldDefinitionInput): Observable<CustomFieldDefinition> {
    const mutation = `
      mutation CreateCustomFieldDefinition($input: CustomFieldDefinitionInput!) {
        createCustomFieldDefinition(input: $input) {
          id
          entityType
          key
          label
          fieldType
          refEntityType
          required
          filterable
          options
          description
          placeholder
          defaultValue
          validationRegex
          validationMessage
          minValue
          maxValue
          conditionalFieldKey
          conditionalValue
          orderIndex
          createdAt
          updatedAt
        }
      }
    `;

    return this.api
      .graphql<{ createCustomFieldDefinition: CustomFieldDefinition }>(mutation, { input })
      .pipe(map((res) => res.createCustomFieldDefinition));
  }

  updateDefinition(id: string, input: CustomFieldDefinitionInput): Observable<CustomFieldDefinition> {
    const mutation = `
      mutation UpdateCustomFieldDefinition($id: UUID!, $input: CustomFieldDefinitionInput!) {
        updateCustomFieldDefinition(id: $id, input: $input) {
          id
          entityType
          key
          label
          fieldType
          refEntityType
          required
          filterable
          options
          description
          placeholder
          defaultValue
          validationRegex
          validationMessage
          minValue
          maxValue
          conditionalFieldKey
          conditionalValue
          orderIndex
          createdAt
          updatedAt
        }
      }
    `;

    return this.api
      .graphql<{ updateCustomFieldDefinition: CustomFieldDefinition }>(mutation, { id, input })
      .pipe(map((res) => res.updateCustomFieldDefinition));
  }

  deleteDefinition(id: string): Observable<boolean> {
    const mutation = `
      mutation DeleteCustomFieldDefinition($id: UUID!) {
        deleteCustomFieldDefinition(id: $id)
      }
    `;

    return this.api
      .graphql<{ deleteCustomFieldDefinition: boolean }>(mutation, { id })
      .pipe(map((res) => res.deleteCustomFieldDefinition));
  }

  setFieldValue(input: CustomFieldValueInput): Observable<CustomFieldValue> {
    const mutation = `
      mutation SetCustomFieldValue($input: CustomFieldValueInput!) {
        setCustomFieldValue(input: $input) {
          id
          entityType
          entityId
          valueText
          valueNumber
          valueBool
          valueDate
          valueEntityId
          valueEntityType
          definition {
            id
            key
            label
            fieldType
          }
        }
      }
    `;

    const formattedInput = {
      ...input,
      value: input.value !== undefined && input.value !== null ? String(input.value) : null
    };

    return this.api
      .graphql<{ setCustomFieldValue: CustomFieldValue }>(mutation, { input: formattedInput })
      .pipe(map((res) => res.setCustomFieldValue));
  }

  setFieldValues(inputs: CustomFieldValueInput[]): Observable<CustomFieldValue[]> {
    const mutation = `
      mutation SetCustomFieldValues($inputs: [CustomFieldValueInput!]!) {
        setCustomFieldValues(inputs: $inputs) {
          id
          entityType
          entityId
          valueText
          valueNumber
          valueBool
          valueDate
          valueEntityId
          valueEntityType
          definition {
            id
            key
            label
            fieldType
          }
        }
      }
    `;

    const formattedInputs = inputs.map((inp) => ({
      ...inp,
      value: inp.value !== undefined && inp.value !== null ? String(inp.value) : null
    }));

    return this.api
      .graphql<{ setCustomFieldValues: CustomFieldValue[] }>(mutation, { inputs: formattedInputs })
      .pipe(map((res) => res.setCustomFieldValues));
  }

  deleteFieldValue(id: string): Observable<boolean> {
    const mutation = `
      mutation DeleteCustomFieldValue($id: UUID!) {
        deleteCustomFieldValue(id: $id)
      }
    `;

    return this.api
      .graphql<{ deleteCustomFieldValue: boolean }>(mutation, { id })
      .pipe(map((res) => res.deleteCustomFieldValue));
  }
}