import { Component, signal, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { TabsComponent, TabItem } from '../../shared/components/tabs/tabs.component';

@Component({
  selector: 'app-organization',
  standalone: true,
  imports: [CommonModule, RouterOutlet, TabsComponent],
  templateUrl: './organization.component.html',
  styleUrls: ['./organization.component.scss']
})
export class OrganizationComponent {
  private readonly router = inject(Router);

  readonly tabs: TabItem[] = [
    { id: 'departments', label: 'Departments', icon: 'tablerBuilding' },
    { id: 'positions', label: 'Positions', icon: 'tablerBriefcase' }
  ];

  readonly activeTabId = signal<string>('departments');
  readonly showTabs = signal(true);

  constructor() {
    effect(() => {
      const url = this.router.url;
      const isDetail = url.includes('/departments/') && !url.endsWith('/departments');
      this.showTabs.set(!isDetail);

      if (url.includes('/departments')) {
        this.activeTabId.set('departments');
      } else if (url.includes('/positions')) {
        this.activeTabId.set('positions');
      }
    });
  }

  onTabChange(tabId: string): void {
    this.activeTabId.set(tabId);
    this.router.navigate(['/organization', tabId]);
  }
}
