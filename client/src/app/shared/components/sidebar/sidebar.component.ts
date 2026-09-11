import { Component, signal, computed, inject, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { NgIconComponent } from '@ng-icons/core';
import { AuthService, ThemeService, NotificationService } from '@services';
import { AvatarService } from '@features/profile/avatar/avatar.service';

interface MenuItem {
  label: string;
  icon: string;
  route: string;
  badge?: number;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, NgIconComponent],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly avatarService = inject(AvatarService);
  private readonly notificationService = inject(NotificationService);
  readonly themeService = inject(ThemeService);

  readonly unreadNotificationCount = computed(() => this.notificationService.unreadCount());

  private readonly isCollapsedSignal = signal(true);

  isCollapsed = computed(() => this.isCollapsedSignal());
  isDark = computed(() => this.themeService.isDark());
  collapsedChange = output<boolean>();

  userName = computed(() => {
    const user = this.authService.currentUser();
    return user ? `${user.firstName} ${user.lastName}` : 'Guest User';
  });

  userEmail = computed(() => {
    const user = this.authService.currentUser();
    return user ? user.email : '';
  });

  userInitial = computed(() => {
    const user = this.authService.currentUser();
    return user ? user.firstName.charAt(0).toUpperCase() : '?';
  });

  userAvatarUrl = computed(() => {
    const user = this.authService.currentUser();
    return this.avatarService.getAvatarUrl(user?.id, user?.profile?.avatarPath);
  });

  hasAvatar = computed(() => {
    return !!this.userAvatarUrl();
  });

  menuItems: MenuItem[] = [
    { label: 'Dashboard', icon: 'tablerDashboard', route: '/dashboard' },
    { label: 'Calendar', icon: 'tablerCalendarClock', route: '/calendar' },
    { label: 'Talent', icon: 'tablerBriefcase', route: '/talent' },
    { label: 'Tasks', icon: 'tablerClipboardList', route: '/tasks' },
    { label: 'Members', icon: 'tablerUsers', route: '/members' },
    { label: 'Organization', icon: 'tablerBuildingCommunity', route: '/organization' },
    { label: 'Settings', icon: 'tablerSettings', route: '/settings' }
  ];

  toggleSidebar(): void {
    this.isCollapsedSignal.set(!this.isCollapsed());
    this.collapsedChange.emit(this.isCollapsed());
  }

  closeSidebar(): void {
    if (!this.isCollapsed()) {
      this.isCollapsedSignal.set(true);
      this.collapsedChange.emit(this.isCollapsed());
    }
  }

  goToProfile(): void {
    this.closeSidebar();
    this.router.navigate(['/profile']);
  }

  openNotifications(): void {
    this.notificationService.togglePanel();
  }

  isActive(route: string): boolean {
    return this.router.url === route || this.router.url.startsWith(route + '/') || this.router.url.startsWith(route + '?');
  }


  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  logout(): void {
    this.authService.logout();
  }
}
