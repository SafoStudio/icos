import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent } from '@ng-icons/core';

@Component({
  selector: 'app-custom-fields-stats',
  standalone: true,
  imports: [CommonModule, NgIconComponent],
  templateUrl: './custom-fields-stats.component.html',
  styleUrls: ['./custom-fields-stats.component.scss']
})
export class CustomFieldsStatsComponent {
  @Input() total = 0;
  @Input() entityStats: Record<string, number> = {};
  @Input() conditionalRules = 0;
}
