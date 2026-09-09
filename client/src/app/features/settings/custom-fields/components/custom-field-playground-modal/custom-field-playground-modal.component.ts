import { Component, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { CustomFieldsFormComponent } from '@components';
import { EntityType } from '@models';
import { CustomFieldsFormService } from '../../custom-fields-form.service';

@Component({
  selector: 'app-custom-field-playground-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent, CustomFieldsFormComponent],
  templateUrl: './custom-field-playground-modal.component.html',
  styleUrls: ['./custom-field-playground-modal.component.scss']
})
export class CustomFieldPlaygroundModalComponent {
  readonly formService = inject(CustomFieldsFormService);
  @Output() close = new EventEmitter<void>();

  playgroundEntityType: EntityType = 'CANDIDATE';
  playgroundValues: Record<string, any> = {};

  get entityTypes(): EntityType[] {
    return this.formService.entityTypesList;
  }

  getEntityLabel(entity: EntityType): string {
    return this.formService.entityTypesInfo[entity]?.label || entity;
  }

  getEntityIcon(entity: EntityType): string {
    return this.formService.entityTypesInfo[entity]?.icon || 'tablerForms';
  }
}
