import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { CustomFieldsFormComponent } from '@components';
import { CustomFieldsFormService } from '../../custom-fields-form.service';
import { CustomFieldDefinition, EntityType, FieldType } from '@models';

@Component({
  selector: 'app-custom-field-edit-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent, CustomFieldsFormComponent],
  templateUrl: './custom-field-edit-modal.component.html',
  styleUrls: ['./custom-field-edit-modal.component.scss']
})
export class CustomFieldEditModalComponent {
  readonly formService = inject(CustomFieldsFormService);
  @Input() saving = false;
  @Input() potentialParentFields: CustomFieldDefinition[] = [];
  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<void>();

  get livePreviewDefinition(): CustomFieldDefinition {
    const f = this.formService;
    return {
      id: 'preview',
      entityType: f.entityType(),
      key: f.key() || 'preview_field',
      label: f.label() || 'Field Preview',
      fieldType: f.fieldType(),
      required: f.required(),
      filterable: f.filterable(),
      options: f.options().length > 0 ? f.options() : ['Option A', 'Option B', 'Option C'],
      description: f.description() || null,
      placeholder: f.placeholder() || null,
      defaultValue: f.defaultValue() || null,
      validationRegex: f.validationRegex() || null,
      validationMessage: f.validationMessage() || null,
      minValue: f.minValue(),
      maxValue: f.maxValue(),
      orderIndex: 0
    };
  }

  onLabelInput(): void {
    if (!this.formService.editingId()) {
      const label = this.formService.label();
      const key = label.toLowerCase().trim().replace(/[\s-]+/g, '_').replace(/[^a-z0-9_]/g, '');
      this.formService.key.set(key);
    }
  }

  onValidationPresetChange(): void {
    const preset = this.formService.validationPresets.find(p => p.id === this.formService.selectedValidationPreset());
    if (preset) {
      this.formService.validationRegex.set(preset.regex);
      this.formService.validationMessage.set(preset.message);
    }
  }

  addOption(): void {
    const val = this.formService.newOptionInput().trim();
    if (val && !this.formService.options().includes(val)) {
      this.formService.options.set([...this.formService.options(), val]);
      this.formService.newOptionInput.set('');
    }
  }

  removeOption(opt: string): void {
    this.formService.options.set(this.formService.options().filter(o => o !== opt));
  }

  applyOptionPreset(preset: string[]): void {
    const current = this.formService.options();
    const newOptions = [...current];
    for (const item of preset) {
      if (!newOptions.includes(item)) {
        newOptions.push(item);
      }
    }
    this.formService.options.set(newOptions);
  }

  getEntityTypes(): EntityType[] {
    return this.formService.entityTypesList;
  }

  getFieldTypes(): FieldType[] {
    return this.formService.fieldTypesList;
  }

  getEntityInfo(entity: EntityType) {
    return this.formService.entityTypesInfo[entity] || { label: entity, icon: 'tablerForms', description: '' };
  }

  getFieldTypeInfo(type: FieldType) {
    return this.formService.fieldTypesInfo[type] || { type, label: type, description: '', icon: 'tablerForms', category: 'text' };
  }
}
