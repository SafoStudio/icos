import { Component, signal, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { TabsComponent, type TabItem } from '@components';
import { NgIconComponent } from '@ng-icons/core';

@Component({
  selector: 'app-talent',
  standalone: true,
  imports: [CommonModule, RouterOutlet, TabsComponent, NgIconComponent],
  templateUrl: './talent.component.html',
  styleUrls: ['./talent.component.scss']
})
export class TalentComponent {
  private readonly router = inject(Router);

  readonly tabs: TabItem[] = [
    { id: 'recruitment', label: 'Recruitment', icon: 'tablerBriefcase' },
    { id: 'onboarding', label: 'Onboarding', icon: 'tablerUserPlus' },
    { id: 'offboarding', label: 'Offboarding', icon: 'tablerUserX' }
  ];

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
