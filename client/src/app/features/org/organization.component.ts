import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TabsComponent, TabItem } from '../../shared/components/tabs/tabs.component';
import { DepartmentsComponent } from './departments/departments.component';
import { PositionsComponent } from './positions/positions.component';

@Component({
  selector: 'app-organization',
  standalone: true,
  imports: [CommonModule, TabsComponent, DepartmentsComponent, PositionsComponent],
  templateUrl: './organization.component.html',
  styleUrls: ['./organization.component.scss']
})
export class OrganizationComponent {
  readonly tabs: TabItem[] = [
    { id: 'departments', label: 'Departments', icon: 'tablerBuilding' },
    { id: 'positions', label: 'Positions', icon: 'tablerBriefcase' }
  ];

  readonly activeTabId = signal<string>('departments');

  onTabChange(tabId: string): void {
    this.activeTabId.set(tabId);
  }
}
