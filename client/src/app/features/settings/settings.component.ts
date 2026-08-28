import { Component, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { NgIconComponent } from '@ng-icons/core';
import { TabsComponent, type TabItem } from '@components';
import { AuthService, ThemeService } from '@services';
import { CustomFieldsComponent } from './custom-fields/custom-fields.component';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, RouterModule, NgIconComponent, TabsComponent, CustomFieldsComponent],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent {
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  readonly themeService = inject(ThemeService);

  readonly activeTabId = signal<string>('custom-fields');

  readonly tabs: TabItem[] = [
    { id: 'custom-fields', label: 'Custom Fields & Forms', icon: 'tablerForms' },
    { id: 'general', label: 'General & Organization', icon: 'tablerBuilding' },
    { id: 'preferences', label: 'Appearance & Themes', icon: 'tablerAdjustments' }
  ];

  readonly currentUser = computed(() => this.authService.currentUser());
  readonly isDark = computed(() => this.themeService.isDark());

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }
}
