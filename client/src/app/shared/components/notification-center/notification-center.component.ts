import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NgIconComponent } from '@ng-icons/core';
import { NotificationService } from '@services';
import { type AppNotification } from '@models';

@Component({
  selector: 'app-notification-center',
  standalone: true,
  imports: [CommonModule, NgIconComponent],
  templateUrl: './notification-center.component.html',
  styleUrls: ['./notification-center.component.scss']
})
export class NotificationCenterComponent {
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  readonly isOpen = computed(() => this.notificationService.isPanelOpen());
  readonly notifications = computed(() => this.notificationService.filteredNotifications());
  readonly unreadCount = computed(() => this.notificationService.unreadCount());
  readonly totalCount = computed(() => this.notificationService.notifications().length);
  readonly activeFilter = computed(() => this.notificationService.activeFilter());
  readonly isLoading = computed(() => this.notificationService.isLoading());

  close(): void {
    this.notificationService.closePanel();
  }

  setFilter(filter: 'all' | 'unread'): void {
    this.notificationService.activeFilter.set(filter);
  }

  markAsRead(id: string, event: Event): void {
    event.stopPropagation();
    this.notificationService.markAsRead(id);
  }

  deleteNotification(id: string, event: Event): void {
    event.stopPropagation();
    this.notificationService.deleteNotification(id);
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead();
  }

  goToSettings(): void {
    this.close();
    this.router.navigate(['/settings'], { queryParams: { tab: 'notifications' } });
  }

  onItemClick(notification: AppNotification): void {
    this.notificationService.handleNotificationClick(notification);
  }

  getIcon(type: string): string {
    if (type.includes('CALENDAR')) return 'tablerCalendarClock';
    if (type.includes('TASK_ASSIGNED')) return 'tablerCheckbox';
    if (type.includes('TASK_STATUS')) return 'tablerClipboardList';
    if (type.includes('ONBOARDING')) return 'tablerUserCheck';
    if (type.includes('LEAVE')) return 'tablerClock';
    if (type.includes('CANDIDATE')) return 'tablerBriefcase';
    if (type.includes('PERFORMANCE')) return 'tablerChartBar';
    return 'tablerBell';
  }

  formatTime(isoString: string): string {
    if (!isoString) return '';
    try {
      const date = new Date(isoString);
      const now = new Date();
      const diffMs = now.getTime() - date.getTime();
      const diffSec = Math.floor(diffMs / 1000);
      const diffMin = Math.floor(diffSec / 60);
      const diffHour = Math.floor(diffMin / 60);
      const diffDay = Math.floor(diffHour / 24);

      if (diffSec < 60) return 'Just now';
      if (diffMin < 60) return `${diffMin}m ago`;
      if (diffHour < 24) return `${diffHour}h ago`;
      if (diffDay === 1) return 'Yesterday';
      if (diffDay < 7) return `${diffDay}d ago`;

      return date.toLocaleDateString(undefined, { month: 'short', day: 'numeric' });
    } catch {
      return '';
    }
  }
}
