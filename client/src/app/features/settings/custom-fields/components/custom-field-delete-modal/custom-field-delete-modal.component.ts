import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent } from '@ng-icons/core';
import { CustomFieldDefinition } from '@models';

@Component({
  selector: 'app-custom-field-delete-modal',
  standalone: true,
  imports: [CommonModule, NgIconComponent],
  templateUrl: './custom-field-delete-modal.component.html',
  styleUrls: ['./custom-field-delete-modal.component.scss']
})
export class CustomFieldDeleteModalComponent {
  @Input() field: CustomFieldDefinition | null = null;
  @Input() saving = false;
  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();
}
