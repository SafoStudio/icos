import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { ToastService } from '@services';
import { CalendarService, CalendarEvent } from './calendar.service';

export interface CalendarDay {
  date: Date;
  dateString: string;
  dayNumber: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  events: CalendarEvent[];
}

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.scss']
})
export class CalendarComponent implements OnInit {
  private readonly calendarService = inject(CalendarService);
  private readonly toast = inject(ToastService);

  currentDate = signal<Date>(new Date());
  events = signal<CalendarEvent[]>([]);
  isLoading = signal<boolean>(false);

  showCreateSidebar = signal<boolean>(false);
  selectedEvent = signal<CalendarEvent | null>(null);

  eventForm = {
    title: '',
    description: '',
    date: this.formatDate(new Date()),
    startTime: '10:00',
    endTime: '11:00'
  };

  readonly weekDays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

  monthLabel = computed(() => {
    return this.currentDate().toLocaleString('en-US', { month: 'long', year: 'numeric' });
  });

  calendarDays = computed(() => {
    const current = this.currentDate();
    const year = current.getFullYear();
    const month = current.getMonth();

    const firstDayOfMonth = new Date(year, month, 1);
    const lastDayOfMonth = new Date(year, month + 1, 0);

    let startDayOfWeek = firstDayOfMonth.getDay();
    startDayOfWeek = startDayOfWeek === 0 ? 6 : startDayOfWeek - 1;

    const days: CalendarDay[] = [];
    const todayStr = this.formatDate(new Date());

    const prevMonthLastDay = new Date(year, month, 0).getDate();
    for (let i = startDayOfWeek - 1; i >= 0; i--) {
      const d = new Date(year, month - 1, prevMonthLastDay - i);
      const dateStr = this.formatDate(d);
      days.push({
        date: d,
        dateString: dateStr,
        dayNumber: d.getDate(),
        isCurrentMonth: false,
        isToday: dateStr === todayStr,
        events: this.getEventsForDate(dateStr)
      });
    }

    for (let day = 1; day <= lastDayOfMonth.getDate(); day++) {
      const d = new Date(year, month, day);
      const dateStr = this.formatDate(d);
      days.push({
        date: d,
        dateString: dateStr,
        dayNumber: day,
        isCurrentMonth: true,
        isToday: dateStr === todayStr,
        events: this.getEventsForDate(dateStr)
      });
    }

    const remainingDays = 42 - days.length;
    for (let day = 1; day <= remainingDays; day++) {
      const d = new Date(year, month + 1, day);
      const dateStr = this.formatDate(d);
      days.push({
        date: d,
        dateString: dateStr,
        dayNumber: day,
        isCurrentMonth: false,
        isToday: dateStr === todayStr,
        events: this.getEventsForDate(dateStr)
      });
    }

    return days;
  });

  ngOnInit(): void {
    this.loadMonthEvents();
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
        this.events.set(res.calendarEvents || []);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.toast.error('Failed to load calendar events: ' + (err.message || 'Error'));
        this.isLoading.set(false);
      }
    });
  }

  getEventsForDate(dateStr: string): CalendarEvent[] {
    return this.events().filter(e => {
      const eventDate = this.formatDate(new Date(e.startAt));
      return eventDate === dateStr;
    });
  }

  prevMonth(): void {
    const current = this.currentDate();
    this.currentDate.set(new Date(current.getFullYear(), current.getMonth() - 1, 1));
    this.loadMonthEvents();
  }

  nextMonth(): void {
    const current = this.currentDate();
    this.currentDate.set(new Date(current.getFullYear(), current.getMonth() + 1, 1));
    this.loadMonthEvents();
  }

  goToToday(): void {
    this.currentDate.set(new Date());
    this.loadMonthEvents();
  }

  openCreateSidebar(dateStr?: string): void {
    this.closeEventDetail();
    const targetDate = dateStr || this.formatDate(new Date());
    this.eventForm = {
      title: '',
      description: '',
      date: targetDate,
      startTime: '09:00',
      endTime: '10:00'
    };
    this.showCreateSidebar.set(true);
  }

  closeCreateSidebar(): void {
    this.showCreateSidebar.set(false);
  }

  submitCreateEvent(): void {
    if (!this.eventForm.title) {
      this.toast.error('Event title is required');
      return;
    }

    const startISO = new Date(`${this.eventForm.date}T${this.eventForm.startTime}:00`).toISOString();
    const endISO = new Date(`${this.eventForm.date}T${this.eventForm.endTime}:00`).toISOString();

    const description = this.eventForm.description.trim() || null;

    this.calendarService.createCalendarEvent(
      this.eventForm.title,
      description,
      startISO,
      endISO,
      []
    ).subscribe({
      next: (res) => {
        this.toast.success(`Event "${res.createCalendarEvent.title}" scheduled successfully!`);
        this.closeCreateSidebar();
        this.loadMonthEvents();
      },
      error: (err) => {
        this.toast.error('Failed to create event: ' + (err.message || 'Error'));
      }
    });
  }

  viewEvent(event: CalendarEvent, e: MouseEvent): void {
    e.stopPropagation();
    this.closeCreateSidebar();
    this.selectedEvent.set(event);
  }

  closeEventDetail(): void {
    this.selectedEvent.set(null);
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

  closeAllSidebars(): void {
    this.closeCreateSidebar();
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
}
