import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnInit,
  OnChanges,
  SimpleChanges,
  inject,
  signal,
  computed
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { CustomFieldsService, ToastService } from '@services';
import {
  CustomFieldDefinition,
  CustomFieldValue,
  CustomFieldValueInput,
  EntityType
} from '@models';

@Component({
  selector: 'app-custom-fields-form',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './custom-fields-form.component.html',
  styleUrls: ['./custom-fields-form.component.scss']
})
export class CustomFieldsFormComponent implements OnInit, OnChanges {
  private readonly customFieldsService = inject(CustomFieldsService);
  private readonly toastService = inject(ToastService);

  @Input() entityType!: EntityType;
  @Input() entityId?: string | null;
  @Input() readonly = false;
  @Input() layout: 'grid' | 'vertical' | 'compact' = 'grid';
  @Input() showSaveButton = false;
  @Input() customDefinitions?: CustomFieldDefinition[];

  @Input() set values(val: Record<string, any>) {
    if (val) {
      this.formValues.set({ ...val });
    }
  }

  @Output() valuesChange = new EventEmitter<Record<string, any>>();
  @Output() validChange = new EventEmitter<boolean>();
  @Output() saved = new EventEmitter<CustomFieldValue[]>();

  readonly loading = signal<boolean>(false);
  readonly saving = signal<boolean>(false);
  readonly definitions = signal<CustomFieldDefinition[]>([]);
  readonly formValues = signal<Record<string, any>>({});
  readonly errors = signal<Record<string, string>>({});

  /**
   * Computed list of definitions filtered by conditional visibility rules
   */
  readonly visibleDefinitions = computed(() => {
    const allDefs = this.definitions();
    const currentVals = this.formValues();

    return allDefs.filter((def) => {
      if (!def.conditionalFieldKey) {
        return true;
      }
      const parentVal = currentVals[def.conditionalFieldKey];
      if (parentVal === undefined || parentVal === null) {
        return false;
      }
      const expectedVal = def.conditionalValue;
      if (expectedVal === undefined || expectedVal === null || expectedVal === '') {
        return Boolean(parentVal);
      }
      return String(parentVal).toLowerCase() === String(expectedVal).toLowerCase();
    });
  });

  readonly isFormValid = computed(() => {
    return Object.keys(this.errors()).length === 0;
  });

  ngOnInit(): void {
    if (this.customDefinitions) {
      this.definitions.set(this.sortDefinitions(this.customDefinitions));
      this.initDefaultValues();
    } else if (this.entityType) {
      this.loadFields();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['customDefinitions'] && this.customDefinitions) {
      this.definitions.set(this.sortDefinitions(this.customDefinitions));
      this.initDefaultValues();
      this.validateAll();
    } else if (changes['entityType'] && !changes['entityType'].firstChange) {
      this.loadFields();
    } else if (changes['entityId'] && !changes['entityId'].firstChange && this.entityId) {
      this.loadValues();
    }
  }

  loadFields(): void {
    if (!this.entityType) return;
    this.loading.set(true);

    this.customFieldsService.getDefinitions(this.entityType).subscribe({
      next: (defs) => {
        this.definitions.set(this.sortDefinitions(defs));
        this.initDefaultValues();

        if (this.entityId) {
          this.loadValues();
        } else {
          this.loading.set(false);
          this.validateAll();
        }
      },
      error: (err) => {
        console.error('Error loading custom fields definitions:', err);
        this.loading.set(false);
      }
    });
  }

  loadValues(): void {
    if (!this.entityType || !this.entityId) return;

    this.customFieldsService.getValues(this.entityType, this.entityId).subscribe({
      next: (valuesList) => {
        const vals: Record<string, any> = { ...this.formValues() };
        for (const item of valuesList) {
          if (!item.definition) continue;
          const key = item.definition.key;
          const type = item.definition.fieldType;

          switch (type) {
            case 'NUMBER':
              vals[key] = item.valueNumber ?? null;
              break;
            case 'BOOLEAN':
              vals[key] = item.valueBool ?? false;
              break;
            case 'DATE':
              if (item.valueDate) {
                vals[key] = item.valueDate.split('T')[0];
              }
              break;
            case 'MULTI_SELECT':
              if (item.valueText) {
                try {
                  vals[key] = JSON.parse(item.valueText);
                } catch {
                  vals[key] = item.valueText.split(',').map((s) => s.trim());
                }
              } else {
                vals[key] = [];
              }
              break;
            case 'ENTITY_REF':
              vals[key] = item.valueEntityId ?? null;
              break;
            default:
              vals[key] = item.valueText ?? '';
          }
        }
        this.formValues.set(vals);
        this.loading.set(false);
        this.validateAll();
        this.valuesChange.emit(this.formValues());
      },
      error: (err) => {
        console.error('Error loading custom field values:', err);
        this.loading.set(false);
      }
    });
  }

  private initDefaultValues(): void {
    const current = { ...this.formValues() };
    for (const def of this.definitions()) {
      if (current[def.key] === undefined) {
        if (def.defaultValue !== undefined && def.defaultValue !== null && def.defaultValue !== '') {
          if (def.fieldType === 'BOOLEAN') {
            current[def.key] = def.defaultValue === 'true' || def.defaultValue === '1';
          } else if (def.fieldType === 'NUMBER') {
            current[def.key] = Number(def.defaultValue);
          } else if (def.fieldType === 'MULTI_SELECT') {
            current[def.key] = [def.defaultValue];
          } else {
            current[def.key] = def.defaultValue;
          }
        } else if (def.fieldType === 'BOOLEAN') {
          current[def.key] = false;
        } else if (def.fieldType === 'MULTI_SELECT') {
          current[def.key] = [];
        } else {
          current[def.key] = '';
        }
      }
    }
    this.formValues.set(current);
  }

  private sortDefinitions(defs: CustomFieldDefinition[]): CustomFieldDefinition[] {
    return [...defs].sort((a, b) => {
      const orderA = a.orderIndex ?? 0;
      const orderB = b.orderIndex ?? 0;
      return orderA - orderB;
    });
  }

  onValueChange(key: string, value: any): void {
    const updated = { ...this.formValues(), [key]: value };
    this.formValues.set(updated);
    this.validateField(key, value);
    this.valuesChange.emit(updated);
  }

  toggleMultiSelectOption(key: string, option: string): void {
    const currentList: string[] = Array.isArray(this.formValues()[key])
      ? [...this.formValues()[key]]
      : [];
    const index = currentList.indexOf(option);
    if (index > -1) {
      currentList.splice(index, 1);
    } else {
      currentList.push(option);
    }
    this.onValueChange(key, currentList);
  }

  isOptionSelected(key: string, option: string): boolean {
    const val = this.formValues()[key];
    return Array.isArray(val) && val.includes(option);
  }

  validateField(key: string, value: any): void {
    const def = this.definitions().find((d) => d.key === key);
    if (!def) return;

    const isVisible = this.visibleDefinitions().some((d) => d.key === key);
    if (!isVisible) {
      const currentErrors = { ...this.errors() };
      delete currentErrors[key];
      this.errors.set(currentErrors);
      this.validChange.emit(this.isFormValid());
      return;
    }

    const currentErrors = { ...this.errors() };
    const isBlank =
      value === undefined ||
      value === null ||
      (typeof value === 'string' && value.trim() === '') ||
      (Array.isArray(value) && value.length === 0);

    if (def.required && isBlank) {
      currentErrors[key] = `${def.label} is required`;
    } else if (!isBlank) {
      if (def.fieldType === 'NUMBER') {
        const num = Number(value);
        if (isNaN(num)) {
          currentErrors[key] = 'Must be a valid number';
        } else if (def.minValue !== undefined && def.minValue !== null && num < def.minValue) {
          currentErrors[key] = `Must be at least ${def.minValue}`;
        } else if (def.maxValue !== undefined && def.maxValue !== null && num > def.maxValue) {
          currentErrors[key] = `Cannot exceed ${def.maxValue}`;
        } else {
          delete currentErrors[key];
        }
      } else if (def.fieldType === 'URL') {
        const urlStr = String(value).trim();
        const urlPattern = /^(https?:\/\/)?([\da-z.-]+)\.([a-z.]{2,6})([/\w .-]*)*\/?$/i;
        if (!urlPattern.test(urlStr) && !urlStr.startsWith('http://') && !urlStr.startsWith('https://')) {
          currentErrors[key] = 'Please enter a valid web URL (e.g. https://example.com)';
        } else {
          delete currentErrors[key];
        }
      } else if (def.validationRegex && typeof value === 'string' && value.trim() !== '') {
        try {
          const reg = new RegExp(def.validationRegex);
          if (!reg.test(value)) {
            currentErrors[key] = def.validationMessage || `Invalid format for ${def.label}`;
          } else {
            delete currentErrors[key];
          }
        } catch {
          delete currentErrors[key];
        }
      } else {
        delete currentErrors[key];
      }
    } else {
      delete currentErrors[key];
    }

    this.errors.set(currentErrors);
    this.validChange.emit(this.isFormValid());
  }

  validateAll(): boolean {
    const vals = this.formValues();
    for (const def of this.visibleDefinitions()) {
      this.validateField(def.key, vals[def.key]);
    }
    return this.isFormValid();
  }

  save(): void {
    if (!this.entityType || !this.entityId) {
      this.toastService.error('Entity ID is required to save custom fields');
      return;
    }

    if (!this.validateAll()) {
      this.toastService.error('Please resolve validation errors in custom fields');
      return;
    }

    this.saving.set(true);
    const inputs: CustomFieldValueInput[] = [];
    const vals = this.formValues();

    for (const def of this.visibleDefinitions()) {
      let rawVal = vals[def.key];
      if (def.fieldType === 'MULTI_SELECT' && Array.isArray(rawVal)) {
        rawVal = JSON.stringify(rawVal);
      }
      inputs.push({
        entityType: this.entityType,
        entityId: this.entityId,
        fieldKey: def.key,
        value: rawVal !== undefined && rawVal !== null ? String(rawVal) : ''
      });
    }

    this.customFieldsService.setFieldValues(inputs).subscribe({
      next: (savedValues) => {
        this.saving.set(false);
        this.toastService.success('Custom fields saved successfully');
        this.saved.emit(savedValues);
      },
      error: (err) => {
        console.error('Error saving custom fields:', err);
        this.saving.set(false);
        this.toastService.error(err.message || 'Failed to save custom fields');
      }
    });
  }

  getFieldError(key: string): string | null {
    return this.errors()[key] || null;
  }
}
