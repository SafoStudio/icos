import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { EntityType, FieldType } from '@models';

@Component({
  selector: 'app-custom-fields-toolbar',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './custom-fields-toolbar.component.html',
  styleUrls: ['./custom-fields-toolbar.component.scss']
})
export class CustomFieldsToolbarComponent {
  @Input() selectedEntity: EntityType | 'ALL' = 'ALL';
  @Input() searchQuery = '';
  @Input() filterFieldType: FieldType | 'ALL' = 'ALL';
  @Input() totalDefinitions = 0;
  @Input() entityStats: Record<string, number> = {};
  @Input() entityTypes: EntityType[] = [];
  @Input() fieldTypes: FieldType[] = [];

  @Output() entityChange = new EventEmitter<EntityType | 'ALL'>();
  @Output() searchChange = new EventEmitter<string>();
  @Output() filterTypeChange = new EventEmitter<FieldType | 'ALL'>();
  @Output() resetFilters = new EventEmitter<void>();

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
}
