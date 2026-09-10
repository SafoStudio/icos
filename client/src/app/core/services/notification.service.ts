import { Injectable, inject, signal, computed, effect } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from './api.service';
import { AuthService } from './auth.service';
import { ToastService } from './toast.service';
import { type AppNotification, type NotificationPreference } from '@models';
import { catchError, of, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly api = inject(ApiService);
  private readonly auth = inject(AuthService);
  private readonly toast = inject(ToastService);
  private readonly router = inject(Router);

  readonly notifications = signal<AppNotification[]>([]);
  readonly unreadCount = signal<number>(0);
  readonly preferences = signal<NotificationPreference[]>([]);
  readonly isLoading = signal<boolean>(false);
  readonly isPanelOpen = signal<boolean>(false);
  readonly activeFilter = signal<'all' | 'unread'>('all');

  private eventSource: EventSource | null = null;
  private pollTimer: any = null;
  private reconnectTimer: any = null;

  readonly filteredNotifications = computed(() => {
    const list = this.notifications();
    const filter = this.activeFilter();
    if (filter === 'unread') {
      return list.filter(n => !n.isRead);
    }
    return list;
  });

  constructor() {
    effect(() => {
      const user = this.auth.currentUser();
      if (user) {
        this.init();
      } else {
        this.destroy();
      }
    });
  }

  private init(): void {
    this.loadNotifications();
    this.loadUnreadCount();
    this.loadPreferences();
    this.connectRealtimeStream();
    this.startPolling();
  }

  private destroy(): void {
    this.notifications.set([]);
    this.unreadCount.set(0);
    this.preferences.set([]);
    this.isPanelOpen.set(false);
    this.disconnectRealtimeStream();
    this.stopPolling();
  }

  loadNotifications(limit: number = 25, offset: number = 0): void {
    this.isLoading.set(true);
    const query = `
      query GetNotifications($limit: Int, $offset: Int) {
        notifications(limit: $limit, offset: $offset) {
          id
          userId
          type
          title
          message
          isRead
          createdAt
        }
      }
    `;

    this.api.graphql<{ notifications: AppNotification[] }>(query, { limit, offset })
      .pipe(
        tap(data => {
          this.notifications.set(data.notifications || []);
          this.isLoading.set(false);
        }),
        catchError(err => {
          this.isLoading.set(false);
          return of(null);
        })
      )
      .subscribe();
  }

  loadUnreadCount(): void {
    const query = `
      query GetUnreadCount {
        unreadNotificationCount
      }
    `;

    this.api.graphql<{ unreadNotificationCount: number }>(query)
      .pipe(
        tap(data => {
          if (data && typeof data.unreadNotificationCount === 'number') {
            this.unreadCount.set(data.unreadNotificationCount);
          }
        }),
        catchError(() => of(null))
      )
      .subscribe();
  }

  loadPreferences(): void {
    const query = `
      query GetNotificationPreferences {
        notificationPreferences {
          id
          userId
          type
          inAppEnabled
          emailEnabled
        }
      }
    `;

    this.api.graphql<{ notificationPreferences: NotificationPreference[] }>(query)
      .pipe(
        tap(data => {
          if (data?.notificationPreferences) {
            this.preferences.set(data.notificationPreferences);
          }
        }),
        catchError(() => of(null))
      )
      .subscribe();
  }

  markAsRead(id: string): void {
    const query = `
      mutation MarkNotificationAsRead($id: ID!) {
        markNotificationAsRead(id: $id) {
          id
          isRead
        }
      }
    `;

    // Optimistic UI update
    this.notifications.update(list =>
      list.map(n => n.id === id ? { ...n, isRead: true } : n)
    );
    this.unreadCount.update(count => Math.max(0, count - 1));

    this.api.graphql(query, { id })
      .pipe(catchError(() => of(null)))
      .subscribe();
  }

  markAllAsRead(): void {
    const query = `
      mutation MarkAllNotificationsAsRead {
        markAllNotificationsAsRead
      }
    `;

    // Optimistic UI update
    this.notifications.update(list =>
      list.map(n => ({ ...n, isRead: true }))
    );
    this.unreadCount.set(0);

    this.api.graphql(query)
      .pipe(
        tap(() => this.toast.success('All notifications marked as read', 'Notifications')),
        catchError(() => of(null))
      )
      .subscribe();
  }

  deleteNotification(id: string): void {
    const query = `
      mutation DeleteNotification($id: ID!) {
        deleteNotification(id: $id)
      }
    `;

    const item = this.notifications().find(n => n.id === id);
    if (item && !item.isRead) {
      this.unreadCount.update(c => Math.max(0, c - 1));
    }
    this.notifications.update(list => list.filter(n => n.id !== id));

    this.api.graphql(query, { id })
      .pipe(catchError(() => of(null)))
      .subscribe();
  }

  updatePreference(type: string, inAppEnabled: boolean, emailEnabled: boolean): void {
    const mutation = `
      mutation UpdateNotificationPreference($type: String!, $inAppEnabled: Boolean!, $emailEnabled: Boolean!) {
        updateNotificationPreference(type: $type, inAppEnabled: $inAppEnabled, emailEnabled: $emailEnabled) {
          id
          userId
          type
          inAppEnabled
          emailEnabled
        }
      }
    `;

    // Optimistic update
    this.preferences.update(prefs =>
      prefs.map(p => p.type === type ? { ...p, inAppEnabled, emailEnabled } : p)
    );

    this.api.graphql<{ updateNotificationPreference: NotificationPreference }>(mutation, {
      type,
      inAppEnabled,
      emailEnabled
    }).pipe(
      tap(res => {
        if (res?.updateNotificationPreference) {
          this.toast.success('Notification preferences updated', 'Settings');
        }
      }),
      catchError(err => {
        this.toast.error('Failed to update preference', 'Settings');
        this.loadPreferences();
        return of(null);
      })
    ).subscribe();
  }

  updateAllPreferences(updatedList: Array<{ type: string; inAppEnabled: boolean; emailEnabled: boolean }>): void {
    const mutation = `
      mutation UpdateNotificationPreferences($preferences: [NotificationPreferenceInput!]!) {
        updateNotificationPreferences(preferences: $preferences) {
          id
          userId
          type
          inAppEnabled
          emailEnabled
        }
      }
    `;

    this.api.graphql<{ updateNotificationPreferences: NotificationPreference[] }>(mutation, {
      preferences: updatedList
    }).pipe(
      tap(res => {
        if (res?.updateNotificationPreferences) {
          this.preferences.set(res.updateNotificationPreferences);
          this.toast.success('All notification settings saved', 'Saved');
        }
      }),
      catchError(err => {
        this.toast.error('Failed to save settings', 'Error');
        return of(null);
      })
    ).subscribe();
  }

  togglePanel(): void {
    this.isPanelOpen.set(!this.isPanelOpen());
    if (this.isPanelOpen()) {
      this.loadNotifications();
      this.loadUnreadCount();
    }
  }

  openPanel(): void {
    this.isPanelOpen.set(true);
    this.loadNotifications();
    this.loadUnreadCount();
  }

  closePanel(): void {
    this.isPanelOpen.set(false);
  }

  handleNotificationClick(notification: AppNotification): void {
    if (!notification.isRead) {
      this.markAsRead(notification.id);
    }
    this.closePanel();

    const type = notification.type;
    if (type.includes('CALENDAR')) {
      this.router.navigate(['/calendar']);
    } else if (type.includes('TASK')) {
      this.router.navigate(['/tasks']);
    } else if (type.includes('CANDIDATE')) {
      this.router.navigate(['/talent']);
    } else if (type.includes('LEAVE')) {
      this.router.navigate(['/profile']);
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

  private connectRealtimeStream(): void {
    const token = this.auth.getAccessToken();
    if (!token || typeof EventSource === 'undefined') {
      return;
    }

    this.disconnectRealtimeStream();

    try {
      this.eventSource = new EventSource(`/api/notifications/stream?token=${encodeURIComponent(token)}`);

      this.eventSource.addEventListener('notification', (event: MessageEvent) => {
        try {
          const item: AppNotification = JSON.parse(event.data);
          this.notifications.update(current => [item, ...current]);
          this.unreadCount.update(c => c + 1);
          this.toast.info(item.message || item.title, item.title || 'New Notification');
        } catch (e) {
          console.error('Failed to parse incoming notification event', e);
        }
      });

      this.eventSource.onerror = () => {
        this.disconnectRealtimeStream();
        if (this.auth.isLoggedIn()) {
          this.reconnectTimer = setTimeout(() => this.connectRealtimeStream(), 10000);
        }
      };
    } catch (err) {
      console.warn('Realtime notifications stream initialization failed', err);
    }
  }

  private disconnectRealtimeStream(): void {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
    }
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
  }

  private startPolling(): void {
    this.stopPolling();
    this.pollTimer = setInterval(() => {
      if (this.auth.isLoggedIn()) {
        this.loadUnreadCount();
      }
    }, 30000);
  }

  private stopPolling(): void {
    if (this.pollTimer) {
      clearInterval(this.pollTimer);
      this.pollTimer = null;
    }
  }
}
