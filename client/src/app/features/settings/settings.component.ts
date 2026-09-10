import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { NgIconComponent } from '@ng-icons/core';
import { TabsComponent, type TabItem } from '@components';
import { AuthService, ThemeService } from '@services';
import { CustomFieldsComponent } from './custom-fields/custom-fields.component';
import { NotificationSettingsComponent } from './notification-settings/notification-settings.component';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, RouterModule, NgIconComponent, TabsComponent, CustomFieldsComponent, NotificationSettingsComponent],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly authService = inject(AuthService);
  readonly themeService = inject(ThemeService);

  readonly activeTabId = signal<string>('custom-fields');

  readonly tabs: TabItem[] = [
    { id: 'custom-fields', label: 'Custom Fields & Forms', icon: 'tablerForms' },
    { id: 'notifications', label: 'Notifications & Alerts', icon: 'tablerBell' },
    { id: 'general', label: 'General & Organization', icon: 'tablerBuilding' },
    { id: 'preferences', label: 'Appearance & Themes', icon: 'tablerAdjustments' }
  ];

  ngOnInit(): void {
    const requestedTab = this.route.snapshot.queryParamMap.get('tab');
    if (requestedTab && this.tabs.some(t => t.id === requestedTab)) {
      this.activeTabId.set(requestedTab);
    }
  }

  readonly currentUser = computed(() => this.authService.currentUser());
  readonly isDark = computed(() => this.themeService.isDark());

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }
}
