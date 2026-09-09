import { Injectable, signal } from '@angular/core';
import {
  EntityType, FieldType,
  CustomFieldDefinition, CustomFieldDefinitionInput,
  FIELD_TYPES_INFO, ENTITY_TYPES_INFO
} from '@models';

export interface ValidationPreset {
  id: string;
  label: string;
  regex: string;
  message: string;
}

@Injectable()
export class CustomFieldsFormService {
  // Form state signals
  readonly entityType = signal<EntityType>('CANDIDATE');
  readonly key = signal<string>('');
  readonly label = signal<string>('');
  readonly fieldType = signal<FieldType>('STRING');
  readonly required = signal<boolean>(false);
  readonly filterable = signal<boolean>(true);
  readonly description = signal<string>('');
  readonly placeholder = signal<string>('');
  readonly defaultValue = signal<string>('');
  readonly options = signal<string[]>([]);
  readonly newOptionInput = signal<string>('');
  readonly minValue = signal<number | null>(null);
  readonly maxValue = signal<number | null>(null);
  readonly validationRegex = signal<string>('');
  readonly validationMessage = signal<string>('');
  readonly conditionalFieldKey = signal<string>('');
  readonly conditionalValue = signal<string>('');
  readonly enableConditional = signal<boolean>(false);
  readonly editingId = signal<string | null>(null);
  readonly selectedValidationPreset = signal<string>('none');

  readonly validationPresets: ValidationPreset[] = [
    { id: 'none', label: 'No Custom Validation', regex: '', message: '' },
    {
      id: 'email',
      label: 'Email Format',
      regex: `^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$`,
      message: 'Please enter a valid email address'
    },
    {
      id: 'phone',
      label: 'Phone Number (International)',
      regex: '^\\+?[0-9\\s-()]{7,20}$',
      message: 'Please enter a valid phone number'
    },
    {
      id: 'url',
      label: 'Web Link (HTTP/HTTPS)',
      regex: '^https?:\\/\\/.+',
      message: 'Please enter a valid web URL starting with http:// or https://'
    },
    {
      id: 'numeric_integer',
      label: 'Positive Integer',
      regex: '^[0-9]+$',
      message: 'Must be a positive whole number'
    },
    { id: 'custom', label: 'Custom Regex Pattern', regex: '', message: '' }
  ];

  readonly entityTypesList: EntityType[] = [
    'CANDIDATE', 'JOB', 'USER', 'ONBOARDING', 'DEPARTMENT', 'POSITION', 'COMPANY'
  ];
  readonly fieldTypesList: FieldType[] = [
    'STRING', 'MULTILINE', 'NUMBER', 'DATE', 'BOOLEAN', 'ENUM', 'MULTI_SELECT', 'URL', 'ENTITY_REF'
  ];
  readonly fieldTypesInfo = FIELD_TYPES_INFO;
  readonly entityTypesInfo = ENTITY_TYPES_INFO;

  resetForm(): void {
    this.entityType.set('CANDIDATE');
    this.key.set('');
    this.label.set('');
    this.fieldType.set('STRING');
    this.required.set(false);
    this.filterable.set(true);
    this.description.set('');
    this.placeholder.set('');
    this.defaultValue.set('');
    this.options.set([]);
    this.newOptionInput.set('');
    this.minValue.set(null);
    this.maxValue.set(null);
    this.validationRegex.set('');
    this.validationMessage.set('');
    this.conditionalFieldKey.set('');
    this.conditionalValue.set('');
    this.enableConditional.set(false);
    this.selectedValidationPreset.set('none');
    this.editingId.set(null);
  }

  populateFromDefinition(def: CustomFieldDefinition): void {
    this.editingId.set(def.id);
    this.entityType.set(def.entityType);
    this.key.set(def.key);
    this.label.set(def.label);
    this.fieldType.set(def.fieldType);
    this.required.set(def.required);
    this.filterable.set(def.filterable);
    this.description.set(def.description || '');
    this.placeholder.set(def.placeholder || '');
    this.defaultValue.set(def.defaultValue || '');
    this.options.set(def.options ? [...def.options] : []);
    this.minValue.set(def.minValue ?? null);
    this.maxValue.set(def.maxValue ?? null);

    if (def.validationRegex) {
      const matched = this.validationPresets.find(p => p.regex === def.validationRegex);
      this.selectedValidationPreset.set(matched ? matched.id : 'custom');
      this.validationRegex.set(def.validationRegex);
      this.validationMessage.set(def.validationMessage || '');
    } else {
      this.selectedValidationPreset.set('none');
      this.validationRegex.set('');
      this.validationMessage.set('');
    }

    if (def.conditionalFieldKey) {
      this.enableConditional.set(true);
      this.conditionalFieldKey.set(def.conditionalFieldKey);
      this.conditionalValue.set(def.conditionalValue || '');
    } else {
      this.enableConditional.set(false);
      this.conditionalFieldKey.set('');
      this.conditionalValue.set('');
    }
  }

  getFormInput(): CustomFieldDefinitionInput {
    return {
      entityType: this.entityType(),
      key: this.key().trim(),
      label: this.label().trim(),
      fieldType: this.fieldType(),
      required: this.required(),
      filterable: this.filterable(),
      options: this.options().length > 0 ? this.options() : null,
      description: this.description().trim() || null,
      placeholder: this.placeholder().trim() || null,
      defaultValue: this.defaultValue().trim() || null,
      validationRegex: this.validationRegex().trim() || null,
      validationMessage: this.validationMessage().trim() || null,
      minValue: this.minValue(),
      maxValue: this.maxValue(),
      conditionalFieldKey: this.enableConditional() && this.conditionalFieldKey() ? this.conditionalFieldKey() : null,
      conditionalValue: this.enableConditional() && this.conditionalValue() ? this.conditionalValue().trim() : null
    };
  }
}
