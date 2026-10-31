import { Component, signal, inject, effect, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { TabsComponent, type TabItem } from '@components';
import { NgIconComponent } from '@ng-icons/core';
import { PermissionService } from '@services';

@Component({
  selector: 'app-talent',
  standalone: true,
  imports: [CommonModule, RouterOutlet, TabsComponent, NgIconComponent],
  templateUrl: './talent.component.html',
  styleUrls: ['./talent.component.scss']
})
export class TalentComponent {
  private readonly router = inject(Router);
  private readonly permissionService = inject(PermissionService);

  readonly allTabs: TabItem[] = [
    { id: 'recruitment', label: 'Recruitment', icon: 'tablerBriefcase', color: '#818cf8' },
    { id: 'onboarding',  label: 'Onboarding',  icon: 'tablerUserPlus', color: '#10b981' },
    { id: 'offboarding', label: 'Offboarding', icon: 'tablerUserX',    color: '#f87171' },
  ];

  readonly tabs = computed(() => {
    return this.allTabs.filter(tab => {
      if (this.permissionService.isAdmin() || this.permissionService.isHr()) {
        return true;
      }
      if (tab.id === 'recruitment') return this.permissionService.hasAnyPermission(['recruitment:read', 'recruitment:manage']);
      if (tab.id === 'onboarding') return this.permissionService.hasAnyPermission(['onboarding:read', 'onboarding:manage']);
      if (tab.id === 'offboarding') return this.permissionService.hasAnyPermission(['offboarding:read', 'offboarding:manage']);
      return false;
    });
  });

  readonly activeTabId = signal<string>('recruitment');

  constructor() {
    effect(() => {
      const url = this.router.url;
      if (url.includes('/talent/onboarding') || url.endsWith('/onboarding')) {
        this.activeTabId.set('onboarding');
      } else if (url.includes('/talent/offboarding') || url.endsWith('/offboarding')) {
        this.activeTabId.set('offboarding');
      } else {
        this.activeTabId.set('recruitment');
      }
    });
  }

  onTabChange(tabId: string): void {
    this.activeTabId.set(tabId);
    this.router.navigate(['/talent', tabId]);
  }
}
