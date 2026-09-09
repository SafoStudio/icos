import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent } from '@ng-icons/core';
import { CustomFieldDefinition, EntityType, FieldType } from '@models';

@Component({
  selector: 'app-custom-fields-table',
  standalone: true,
  imports: [CommonModule, NgIconComponent],
  templateUrl: './custom-fields-table.component.html',
  styleUrls: ['./custom-fields-table.component.scss']
})
export class CustomFieldsTableComponent {
  @Input() definitions: CustomFieldDefinition[] = [];
  @Output() edit = new EventEmitter<CustomFieldDefinition>();
  @Output() delete = new EventEmitter<CustomFieldDefinition>();

  getEntityLabel(entity: EntityType): string {
    const map: Record<EntityType, string> = {
      CANDIDATE: 'Candidate', JOB: 'Job', USER: 'User',
      ONBOARDING: 'Onboarding', DEPARTMENT: 'Department', POSITION: 'Position', COMPANY: 'Company'
    };
    return map[entity] || entity;
  }

  getEntityIcon(entity: EntityType): string {
    const map: Record<EntityType, string> = {
      CANDIDATE: 'tablerUserCheck', JOB: 'tablerBriefcase',
      USER: 'tablerUser', ONBOARDING: 'tablerClipboardList',
      DEPARTMENT: 'tablerBuilding', POSITION: 'tablerBriefcase',
      COMPANY: 'tablerBuilding'
    };
    return map[entity] || 'tablerForms';
  }

  getFieldTypeLabel(type: FieldType): string {
    return type;
  }

  getFieldTypeIcon(type: FieldType): string {
    // FIELD_TYPES_INFO
    return 'tablerForms';
  }
}
