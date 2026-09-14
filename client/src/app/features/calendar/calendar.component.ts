import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { ToastService, AuthService } from '@services';
import {
  CalendarService,
  CalendarEvent,
  CalendarParticipant,
} from './calendar.service';

export type RsvpStatus = 'ACCEPTED' | 'DECLINED' | 'PENDING';
export type RsvpResponse = Extract<RsvpStatus, 'ACCEPTED' | 'DECLINED'>;

export interface CalendarDay {
  date: Date;
  dateString: string;
  dayNumber: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  isWeekend: boolean;
  events: CalendarEvent[];
}

interface EventFormModel {
  title: string;
  description: string;
  date: string;
  startTime: string;
  endTime: string;
}

const WEEK_DAYS = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'] as const;
const DAYS_IN_GRID = 42;
const DEFAULT_START_TIME = '09:00';
const DEFAULT_END_TIME = '10:00';

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.scss'],
})
export class CalendarComponent implements OnInit {
  private readonly calendarService = inject(CalendarService);
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);

  protected readonly Math = Math;

  readonly currentUser = computed(() => this.authService.currentUser());

  readonly currentDate = signal<Date>(new Date());
  readonly events = signal<CalendarEvent[]>([]);

  readonly isLoading = signal<boolean>(false);
  readonly isSubmitting = signal<boolean>(false);

  readonly showCreateOrEditSidebar = signal<boolean>(false);
  readonly isEditMode = signal<boolean>(false);
  readonly editingEventId = signal<string | null>(null);
  readonly selectedEvent = signal<CalendarEvent | null>(null);

  readonly availableMembers = signal<CalendarParticipant[]>([]);
  readonly memberSearchQuery = signal<string>('');
  readonly selectedParticipantIds = signal<string[]>([]);

  eventForm: EventFormModel = this.createEmptyForm();

  readonly weekDays = WEEK_DAYS;

  readonly monthLabel = computed(() =>
    this.currentDate().toLocaleString('en-US', { month: 'long', year: 'numeric' })
  );

  readonly monthName = computed(() =>
    this.currentDate().toLocaleString('en-US', { month: 'long' })
  );

  readonly monthYear = computed(() => String(this.currentDate().getFullYear()));

  readonly filteredMembers = computed(() => {
    const query = this.memberSearchQuery().toLowerCase().trim();
    const currentUserId = this.currentUser()?.id;

    const all = this.availableMembers().filter((m) => m.id !== currentUserId);
    if (!query) return all;

    return all.filter((m) => {
      const fullName = `${m.firstName ?? ''} ${m.lastName ?? ''}`.toLowerCase();
      const email = (m.email ?? '').toLowerCase();
      const role = (m.role ?? '').toLowerCase();
      return fullName.includes(query) || email.includes(query) || role.includes(query);
    });
  });

  readonly selectedMembersList = computed(() => {
    const selectedIds = new Set(this.selectedParticipantIds());
    return this.availableMembers().filter((m) => selectedIds.has(m.id));
  });

  readonly calendarDays = computed<CalendarDay[]>(() => {
    const current = this.currentDate();
    const year = current.getFullYear();
    const month = current.getMonth();

    const firstDayOfMonth = new Date(year, month, 1);
    const lastDayOfMonth = new Date(year, month + 1, 0);
    const todayStr = this.formatDate(new Date());

    const rawDow = firstDayOfMonth.getDay();
    const leadingDays = rawDow === 0 ? 6 : rawDow - 1;

    const days: CalendarDay[] = [];
    const pushDay = (date: Date, isCurrentMonth: boolean): void => {
      const dateStr = this.formatDate(date);
      days.push({
        date,
        dateString: dateStr,
        dayNumber: date.getDate(),
        isCurrentMonth,
        isToday: dateStr === todayStr,
        isWeekend: this.isWeekend(date),
        events: this.getEventsForDate(dateStr),
      });
    };

    const prevMonthLastDay = new Date(year, month, 0).getDate();
    for (let i = leadingDays - 1; i >= 0; i--) {
      pushDay(new Date(year, month - 1, prevMonthLastDay - i), false);
    }

    const lastDayNumber = lastDayOfMonth.getDate();
    for (let day = 1; day <= lastDayNumber; day++) {
      pushDay(new Date(year, month, day), true);
    }

    const remaining = DAYS_IN_GRID - days.length;
    for (let day = 1; day <= remaining; day++) {
      pushDay(new Date(year, month + 1, day), false);
    }

    return days;
  });

  ngOnInit(): void {
    this.loadMonthEvents();
    this.loadCompanyMembers();
  }

  loadCompanyMembers(): void {
    const companyId = this.currentUser()?.companyId;
    if (!companyId) return;

    this.calendarService.getCompanyMembers(companyId).subscribe({
      next: (members) => this.availableMembers.set(members),
      error: (err) => {
        console.error('Failed to load company members for calendar:', err);
      },
    });
  }

  loadMonthEvents(): void {
    this.isLoading.set(true);

    const current = this.currentDate();
    const year = current.getFullYear();
    const month = current.getMonth();

    const startRange = new Date(year, month - 1, 1).toISOString();
    const endRange = new Date(year, month + 2, 0, 23, 59, 59).toISOString();

    this.calendarService.getCalendarEvents(startRange, endRange).subscribe({
      next: (res) => {
        const list = res.calendarEvents ?? [];
        this.events.set(list);
        this.isLoading.set(false);
        this.refreshSelectedEvent(list);
      },
      error: (err) => {
        this.toast.error('Failed to load calendar events: ' + (err.message ?? 'Error'));
        this.isLoading.set(false);
      },
    });
  }

  private refreshSelectedEvent(list: CalendarEvent[]): void {
    const current = this.selectedEvent();
    if (!current) return;

    const updated = list.find((e) => e.id === current.id);
    if (updated) {
      this.selectedEvent.set(updated);
    }
  }

  private getEventsForDate(dateStr: string): CalendarEvent[] {
    return this.events().filter(
      (e) => this.formatDate(new Date(e.startAt)) === dateStr
    );
  }

  private isWeekend(date: Date): boolean {
    const dow = date.getDay();
    return dow === 0 || dow === 6;
  }

  prevMonth(): void {
    const d = this.currentDate();
    this.currentDate.set(new Date(d.getFullYear(), d.getMonth() - 1, 1));
    this.loadMonthEvents();
  }

  nextMonth(): void {
    const d = this.currentDate();
    this.currentDate.set(new Date(d.getFullYear(), d.getMonth() + 1, 1));
    this.loadMonthEvents();
  }

  goToToday(): void {
    this.currentDate.set(new Date());
    this.loadMonthEvents();
  }

  openCreateSidebar(dateStr?: string): void {
    this.closeEventDetail();
    this.isEditMode.set(false);
    this.editingEventId.set(null);
    this.memberSearchQuery.set('');
    this.selectedParticipantIds.set([]);
    this.eventForm = this.createEmptyForm(dateStr ?? this.formatDate(new Date()));
    this.showCreateOrEditSidebar.set(true);
  }

  openEditSidebar(event: CalendarEvent): void {
    this.closeEventDetail();
    this.isEditMode.set(true);
    this.editingEventId.set(event.id);
    this.memberSearchQuery.set('');

    const startDate = new Date(event.startAt);
    const endDate = new Date(event.endAt);
    const currentUserId = this.currentUser()?.id;

    const participantIds = (event.participantIds ?? []).filter(
      (id) => id !== event.userId && id !== currentUserId
    );
    this.selectedParticipantIds.set(participantIds);

    this.eventForm = {
      title: event.title,
      description: event.description ?? '',
      date: this.formatDate(startDate),
      startTime: this.formatTime24(startDate),
      endTime: this.formatTime24(endDate),
    };
    this.showCreateOrEditSidebar.set(true);
  }

  closeCreateOrEditSidebar(): void {
    this.showCreateOrEditSidebar.set(false);
    this.isEditMode.set(false);
    this.editingEventId.set(null);
  }

  toggleParticipant(memberId: string): void {
    const current = this.selectedParticipantIds();
    const next = current.includes(memberId)
      ? current.filter((id) => id !== memberId)
      : [...current, memberId];
    this.selectedParticipantIds.set(next);
  }

  isParticipantSelected(memberId: string): boolean {
    return this.selectedParticipantIds().includes(memberId);
  }

  removeParticipant(memberId: string): void {
    this.selectedParticipantIds.set(
      this.selectedParticipantIds().filter((id) => id !== memberId)
    );
  }

  submitEventForm(): void {
    const title = this.eventForm.title?.trim();
    if (!title) {
      this.toast.error('Event title is required');
      return;
    }

    const startISO = new Date(
      `${this.eventForm.date}T${this.eventForm.startTime}:00`
    ).toISOString();
    const endISO = new Date(
      `${this.eventForm.date}T${this.eventForm.endTime}:00`
    ).toISOString();
    const description = this.eventForm.description.trim() || null;
    const participantIds = this.selectedParticipantIds();

    this.isSubmitting.set(true);

    if (this.isEditMode()) {
      const eventId = this.editingEventId();
      if (!eventId) {
        this.isSubmitting.set(false);
        return;
      }

      this.calendarService
        .updateCalendarEvent(eventId, title, description, startISO, endISO, participantIds)
        .subscribe({
          next: (res) => {
            this.toast.success(`Event "${res.updateCalendarEvent.title}" updated successfully!`);
            this.isSubmitting.set(false);
            this.closeCreateOrEditSidebar();
            this.loadMonthEvents();
          },
          error: (err) => {
            this.toast.error('Failed to update event: ' + (err.message ?? 'Error'));
            this.isSubmitting.set(false);
          },
        });
      return;
    }

    this.calendarService
      .createCalendarEvent(title, description, startISO, endISO, participantIds)
      .subscribe({
        next: (res) => {
          this.toast.success(`Event "${res.createCalendarEvent.title}" scheduled successfully!`);
          this.isSubmitting.set(false);
          this.closeCreateOrEditSidebar();
          this.loadMonthEvents();
        },
        error: (err) => {
          this.toast.error('Failed to create event: ' + (err.message ?? 'Error'));
          this.isSubmitting.set(false);
        },
      });
  }

  submitAttendance(status: RsvpResponse): void {
    const event = this.selectedEvent();
    if (!event) return;

    this.isSubmitting.set(true);
    this.calendarService.updateEventAttendance(event.id, status).subscribe({
      next: (res) => {
        const updated = res.updateEventAttendance;
        this.selectedEvent.set(updated);
        this.isSubmitting.set(false);
        this.toast.success(`Response updated: ${status === 'ACCEPTED' ? 'Going' : 'Declined'}`);
        this.loadMonthEvents();
      },
      error: (err) => {
        this.toast.error('Failed to update RSVP: ' + (err.message ?? 'Error'));
        this.isSubmitting.set(false);
      },
    });
  }

  deleteCurrentEditingEvent(): void {
    const eventId = this.editingEventId();
    if (!eventId) return;
    this.deleteEventById(eventId, this.eventForm.title || 'Event');
  }

  deleteEvent(event: CalendarEvent): void {
    this.deleteEventById(event.id, event.title);
  }

  deleteEventById(id: string, title: string): void {
    if (!confirm(`Are you sure you want to delete event "${title}"?`)) {
      return;
    }

    this.isSubmitting.set(true);
    this.calendarService.deleteCalendarEvent(id).subscribe({
      next: () => {
        this.toast.success('Event deleted successfully');
        this.isSubmitting.set(false);
        this.closeAllSidebars();
        this.loadMonthEvents();
      },
      error: (err) => {
        this.toast.error('Failed to delete event: ' + (err.message ?? 'Error'));
        this.isSubmitting.set(false);
      },
    });
  }

  viewEvent(event: CalendarEvent, e: MouseEvent): void {
    e.stopPropagation();
    this.closeCreateOrEditSidebar();
    this.selectedEvent.set(event);
  }

  closeEventDetail(): void {
    this.selectedEvent.set(null);
  }

  isOrganizer(event: CalendarEvent | null | undefined): boolean {
    if (!event) return false;
    const currentUserId = this.currentUser()?.id;
    return event.userId === currentUserId || event.organizer?.id === currentUserId;
  }

  getCurrentUserStatus(event: CalendarEvent | null | undefined): RsvpStatus {
    if (!event) return 'PENDING';
    if (this.isOrganizer(event)) return 'ACCEPTED';

    if (event.currentUserStatus) {
      return event.currentUserStatus as RsvpStatus;
    }

    const currentUserId = this.currentUser()?.id;
    if (event.participantDetails && currentUserId) {
      const match = event.participantDetails.find((p) => p.userId === currentUserId);
      if (match) return match.status;
    }

    return 'PENDING';
  }

  getParticipantStatus(event: CalendarEvent, userId: string): RsvpStatus {
    if (userId === event.userId) return 'ACCEPTED';

    if (event.participantDetails) {
      const match = event.participantDetails.find((p) => p.userId === userId);
      if (match) return match.status;
    }

    return 'PENDING';
  }

  getUserAvatarUrl(
    user?: CalendarParticipant | { id?: string; profile?: { avatarPath?: string } } | null
  ): string | null {
    const avatarPath = user?.profile?.avatarPath;
    if (!avatarPath) return null;
    if (avatarPath.startsWith('http')) return avatarPath;
    if (user?.id) return `/api/storage/avatar/${user.id}`;
    return null;
  }

  getUserInitials(
    nameOrUser?: CalendarParticipant | { firstName?: string; lastName?: string; email?: string } | null
  ): string {
    if (!nameOrUser) return 'U';

    const first = nameOrUser.firstName?.charAt(0) ?? '';
    const last = nameOrUser.lastName?.charAt(0) ?? '';
    if (first || last) return (first + last).toUpperCase();

    return (nameOrUser.email?.charAt(0) ?? 'U').toUpperCase();
  }

  getUserDisplayName(user?: CalendarParticipant | null): string {
    if (!user) return 'Colleague';
    if (user.firstName || user.lastName) {
      return `${user.firstName ?? ''} ${user.lastName ?? ''}`.trim();
    }
    return user.email || 'Colleague';
  }

  formatDate(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  formatTime(isoString: string): string {
    if (!isoString) return '';
    try {
      const d = new Date(isoString);
      return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch {
      return isoString;
    }
  }

  formatTime24(d: Date): string {
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  closeAllSidebars(): void {
    this.closeCreateOrEditSidebar();
    this.closeEventDetail();
  }

  onOverlayClick(): void {
    this.closeAllSidebars();
  }

  onSidebarKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      this.closeAllSidebars();
    }
  }

  private createEmptyForm(date?: string): EventFormModel {
    return {
      title: '',
      description: '',
      date: date ?? this.formatDate(new Date()),
      startTime: DEFAULT_START_TIME,
      endTime: DEFAULT_END_TIME,
    };
  }
}
