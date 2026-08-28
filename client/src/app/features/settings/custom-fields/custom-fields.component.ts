import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { CustomFieldsService, ToastService } from '@services';
import { CustomFieldsFormComponent } from '@components';
import {
  CustomFieldDefinition,
  CustomFieldDefinitionInput,
  EntityType,
  FieldType,
  FIELD_TYPES_INFO,
  ENTITY_TYPES_INFO,
  FieldTypeMeta
} from '@models';

interface ValidationPreset {
  id: string;
  label: string;
  regex: string;
  message: string;
}

@Component({
  selector: 'app-custom-fields-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent, CustomFieldsFormComponent],
  templateUrl: './custom-fields.component.html',
  styleUrls: ['./custom-fields.component.scss']
})
export class CustomFieldsComponent implements OnInit {
  private readonly customFieldsService = inject(CustomFieldsService);
  private readonly toastService = inject(ToastService);

  readonly loading = signal<boolean>(true);
  readonly saving = signal<boolean>(false);
  readonly definitions = signal<CustomFieldDefinition[]>([]);
  readonly selectedEntityType = signal<EntityType | 'ALL'>('ALL');
  readonly searchQuery = signal<string>('');
  readonly filterFieldType = signal<FieldType | 'ALL'>('ALL');
  readonly showEditModal = signal<boolean>(false);
  readonly showDeleteModal = signal<boolean>(false);
  readonly showPlaygroundModal = signal<boolean>(false);
  readonly fieldToDelete = signal<CustomFieldDefinition | null>(null);
  readonly editingFieldId = signal<string | null>(null);

  formEntityType: EntityType = 'CANDIDATE';
  formKey: string = '';
  formLabel: string = '';
  formFieldType: FieldType = 'STRING';
  formRequired: boolean = false;
  formFilterable: boolean = true;
  formDescription: string = '';
  formPlaceholder: string = '';
  formDefaultValue: string = '';
  formOptions: string[] = [];
  newOptionInput: string = '';
  formMinValue: number | null = null;
  formMaxValue: number | null = null;
  selectedValidationPreset: string = 'none';
  formValidationRegex: string = '';
  formValidationMessage: string = '';
  enableConditional: boolean = false;
  formConditionalFieldKey: string = '';
  formConditionalValue: string = '';
  playgroundEntityType: EntityType = 'CANDIDATE';
  playgroundValues: Record<string, any> = {};

  readonly entityTypesList: EntityType[] = [
    'CANDIDATE',
    'JOB',
    'USER',
    'ONBOARDING',
    'DEPARTMENT',
    'POSITION',
    'COMPANY'
  ];

  readonly fieldTypesList: FieldType[] = [
    'STRING',
    'MULTILINE',
    'NUMBER',
    'DATE',
    'BOOLEAN',
    'ENUM',
    'MULTI_SELECT',
    'URL',
    'ENTITY_REF'
  ];

  readonly fieldTypesInfo = FIELD_TYPES_INFO;
  readonly entityTypesInfo = ENTITY_TYPES_INFO;

  readonly validationPresets: ValidationPreset[] = [
    { id: 'none', label: 'No Custom Validation', regex: '', message: '' },
    {
      id: 'email',
      label: 'Email Format',
      regex: String.raw`^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$`,
      message: 'Please enter a valid email address'
    },
    {
      id: 'phone',
      label: 'Phone Number (International)',
      regex: String.raw`^\+?[0-9\s-()]{7,20}$`,
      message: 'Please enter a valid phone number'
    },
    {
      id: 'url',
      label: 'Web Link (HTTP/HTTPS)',
      regex: String.raw`^https?:\/\/.+`,
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

  // Computed Stats
  readonly totalFieldsCount = computed(() => this.definitions().length);

  readonly entityStats = computed(() => {
    const map: Record<string, number> = {};
    for (const def of this.definitions()) {
      map[def.entityType] = (map[def.entityType] || 0) + 1;
    }
    return map;
  });

  readonly requiredFieldsCount = computed(() => {
    return this.definitions().filter((d) => d.required).length;
  });

  readonly conditionalRulesCount = computed(() => {
    return this.definitions().filter((d) => !!d.conditionalFieldKey).length;
  });

  // Filtered definitions
  readonly filteredDefinitions = computed(() => {
    let list = this.definitions();
    const entity = this.selectedEntityType();
    const search = this.searchQuery().trim().toLowerCase();
    const type = this.filterFieldType();

    if (entity !== 'ALL') {
      list = list.filter((d) => d.entityType === entity);
    }

    if (type !== 'ALL') {
      list = list.filter((d) => d.fieldType === type);
    }

    if (search) {
      list = list.filter(
        (d) =>
          d.label.toLowerCase().includes(search) ||
          d.key.toLowerCase().includes(search) ||
          (d.description && d.description.toLowerCase().includes(search))
      );
    }

    return list;
  });

  // Potential parent fields for conditional logic
  readonly potentialParentFields = computed(() => {
    return this.definitions().filter(
      (d) => d.entityType === this.formEntityType && d.key !== this.formKey
    );
  });

  // Live preview definition for inside modal
  readonly livePreviewDefinition = computed<CustomFieldDefinition>(() => {
    return {
      id: 'preview',
      entityType: this.formEntityType,
      key: this.formKey || 'preview_field',
      label: this.formLabel || 'Field Preview',
      fieldType: this.formFieldType,
      required: this.formRequired,
      filterable: this.formFilterable,
      options: this.formOptions.length > 0 ? this.formOptions : ['Option A', 'Option B', 'Option C'],
      description: this.formDescription || null,
      placeholder: this.formPlaceholder || null,
      defaultValue: this.formDefaultValue || null,
      validationRegex: this.formValidationRegex || null,
      validationMessage: this.formValidationMessage || null,
      minValue: this.formMinValue,
      maxValue: this.formMaxValue,
      orderIndex: 0
    };
  });

  ngOnInit(): void {
    this.loadDefinitions();
  }

  loadDefinitions(): void {
    this.loading.set(true);
    this.customFieldsService.getAllDefinitions().subscribe({
      next: (defs) => {
        this.definitions.set(defs);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error loading custom field definitions:', err);
        this.toastService.error('Failed to load custom field definitions');
        this.loading.set(false);
      }
    });
  }

  openCreateModal(entityType?: EntityType): void {
    this.editingFieldId.set(null);
    this.formEntityType = entityType || (this.selectedEntityType() === 'ALL' ? 'CANDIDATE' : (this.selectedEntityType() as EntityType));
    this.formKey = '';
    this.formLabel = '';
    this.formFieldType = 'STRING';
    this.formRequired = false;
    this.formFilterable = true;
    this.formDescription = '';
    this.formPlaceholder = '';
    this.formDefaultValue = '';
    this.formOptions = [];
    this.newOptionInput = '';
    this.formMinValue = null;
    this.formMaxValue = null;
    this.selectedValidationPreset = 'none';
    this.formValidationRegex = '';
    this.formValidationMessage = '';
    this.enableConditional = false;
    this.formConditionalFieldKey = '';
    this.formConditionalValue = '';
    this.showEditModal.set(true);
  }

  openEditModal(def: CustomFieldDefinition): void {
    this.editingFieldId.set(def.id);
    this.formEntityType = def.entityType;
    this.formKey = def.key;
    this.formLabel = def.label;
    this.formFieldType = def.fieldType;
    this.formRequired = def.required;
    this.formFilterable = def.filterable;
    this.formDescription = def.description || '';
    this.formPlaceholder = def.placeholder || '';
    this.formDefaultValue = def.defaultValue || '';
    this.formOptions = def.options ? [...def.options] : [];
    this.newOptionInput = '';
    this.formMinValue = def.minValue ?? null;
    this.formMaxValue = def.maxValue ?? null;

    if (def.validationRegex) {
      const matched = this.validationPresets.find((p) => p.regex === def.validationRegex);
      this.selectedValidationPreset = matched ? matched.id : 'custom';
      this.formValidationRegex = def.validationRegex;
      this.formValidationMessage = def.validationMessage || '';
    } else {
      this.selectedValidationPreset = 'none';
      this.formValidationRegex = '';
      this.formValidationMessage = '';
    }

    if (def.conditionalFieldKey) {
      this.enableConditional = true;
      this.formConditionalFieldKey = def.conditionalFieldKey;
      this.formConditionalValue = def.conditionalValue || '';
    } else {
      this.enableConditional = false;
      this.formConditionalFieldKey = '';
      this.formConditionalValue = '';
    }

    this.showEditModal.set(true);
  }

  closeEditModal(): void {
    this.showEditModal.set(false);
  }

  onLabelInput(): void {
    if (!this.editingFieldId()) {
      this.formKey = this.formLabel
        .toLowerCase()
        .trim()
        .replace(/[\s-]+/g, '_')
        .replace(/[^a-z0-9_]/g, '');
    }
  }

  onValidationPresetChange(): void {
    const preset = this.validationPresets.find((p) => p.id === this.selectedValidationPreset);
    if (preset) {
      this.formValidationRegex = preset.regex;
      this.formValidationMessage = preset.message;
    }
  }

  addOption(): void {
    const val = this.newOptionInput.trim();
    if (val && !this.formOptions.includes(val)) {
      this.formOptions.push(val);
      this.newOptionInput = '';
    }
  }

  removeOption(opt: string): void {
    this.formOptions = this.formOptions.filter((o) => o !== opt);
  }

  applyOptionPreset(preset: string[]): void {
    for (const item of preset) {
      if (!this.formOptions.includes(item)) {
        this.formOptions.push(item);
      }
    }
  }

  saveDefinition(): void {
    if (!this.formLabel.trim()) {
      this.toastService.error('Please enter a field label');
      return;
    }
    if (!this.formKey.trim()) {
      this.toastService.error('Please enter a field key identifier');
      return;
    }

    if (
      (this.formFieldType === 'ENUM' || this.formFieldType === 'MULTI_SELECT') &&
      this.formOptions.length === 0
    ) {
      this.toastService.error('Please add at least one option for select fields');
      return;
    }

    this.saving.set(true);

    const input: CustomFieldDefinitionInput = {
      entityType: this.formEntityType,
      key: this.formKey.trim(),
      label: this.formLabel.trim(),
      fieldType: this.formFieldType,
      required: this.formRequired,
      filterable: this.formFilterable,
      options: this.formOptions.length > 0 ? this.formOptions : null,
      description: this.formDescription.trim() || null,
      placeholder: this.formPlaceholder.trim() || null,
      defaultValue: this.formDefaultValue.trim() || null,
      validationRegex: this.formValidationRegex.trim() || null,
      validationMessage: this.formValidationMessage.trim() || null,
      minValue: this.formMinValue,
      maxValue: this.formMaxValue,
      conditionalFieldKey: this.enableConditional && this.formConditionalFieldKey ? this.formConditionalFieldKey : null,
      conditionalValue: this.enableConditional && this.formConditionalValue ? this.formConditionalValue.trim() : null
    };

    const isEdit = !!this.editingFieldId();

    if (isEdit) {
      this.customFieldsService.updateDefinition(this.editingFieldId()!, input).subscribe({
        next: () => {
          this.saving.set(false);
          this.showEditModal.set(false);
          this.toastService.success(`Custom field "${input.label}" updated successfully!`);
          this.loadDefinitions();
        },
        error: (err) => {
          console.error('Error updating definition:', err);
          this.saving.set(false);
          this.toastService.error(err.message || 'Failed to update custom field');
        }
      });
    } else {
      this.customFieldsService.createDefinition(input).subscribe({
        next: () => {
          this.saving.set(false);
          this.showEditModal.set(false);
          this.toastService.success(`Custom field "${input.label}" created successfully!`);
          this.loadDefinitions();
        },
        error: (err) => {
          console.error('Error creating definition:', err);
          this.saving.set(false);
          this.toastService.error(err.message || 'Failed to create custom field');
        }
      });
    }
  }

  confirmDelete(def: CustomFieldDefinition): void {
    this.fieldToDelete.set(def);
    this.showDeleteModal.set(true);
  }

  deleteDefinition(): void {
    const def = this.fieldToDelete();
    if (!def) return;

    this.saving.set(true);
    this.customFieldsService.deleteDefinition(def.id).subscribe({
      next: () => {
        this.saving.set(false);
        this.showDeleteModal.set(false);
        this.fieldToDelete.set(null);
        this.toastService.success(`Custom field "${def.label}" removed successfully`);
        this.loadDefinitions();
      },
      error: (err) => {
        console.error('Error deleting custom field:', err);
        this.saving.set(false);
        this.toastService.error(err.message || 'Failed to delete custom field');
      }
    });
  }

  openPlayground(entityType?: EntityType): void {
    this.playgroundEntityType = entityType || (this.selectedEntityType() === 'ALL' ? 'CANDIDATE' : (this.selectedEntityType() as EntityType));
    this.playgroundValues = {};
    this.showPlaygroundModal.set(true);
  }

  closePlayground(): void {
    this.showPlaygroundModal.set(false);
  }

  getFieldTypeInfo(type: FieldType): FieldTypeMeta {
    return this.fieldTypesInfo[type] || {
      type,
      label: type,
      description: '',
      icon: 'tablerForms',
      category: 'text'
    };
  }

  getEntityInfo(entity: EntityType) {
    return this.entityTypesInfo[entity] || {
      label: entity,
      icon: 'tablerForms',
      description: ''
    };
  }
}
