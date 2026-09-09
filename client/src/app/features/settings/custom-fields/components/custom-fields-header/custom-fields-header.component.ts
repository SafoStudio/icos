import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent } from '@ng-icons/core';

@Component({
  selector: 'app-custom-fields-header',
  standalone: true,
  imports: [CommonModule, NgIconComponent],
  templateUrl: './custom-fields-header.component.html',
  styleUrls: ['./custom-fields-header.component.scss']
})
export class CustomFieldsHeaderComponent {
  @Output() openCreate = new EventEmitter<void>();
  @Output() openPlayground = new EventEmitter<void>();
}
