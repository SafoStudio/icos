import { Component, signal, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { NgIconComponent } from '@ng-icons/core';
import { TabItem } from '../../shared/components/tabs/tabs.component';

@Component({
  selector: 'app-organization',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, NgIconComponent],
  templateUrl: './organization.component.html',
  styleUrls: ['./organization.component.scss']
})
export class OrganizationComponent {
  public router = inject(Router);

  readonly tabs: TabItem[] = [
    { id: 'departments', label: 'Departments', icon: 'tablerBuilding' },
    { id: 'positions', label: 'Positions', icon: 'tablerBriefcase' }
  ];

  readonly showTabs = signal(true);

  constructor() {
    effect(() => {
      const url = this.router.url;
      const isDetail = url.includes('/departments/') && !url.endsWith('/departments');
      this.showTabs.set(!isDetail);
    });
  }

  onTabChange(tabId: string): void {
    this.router.navigate(['/organization', tabId]);
  }
}
