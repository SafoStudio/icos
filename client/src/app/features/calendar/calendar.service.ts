import { Injectable, inject } from '@angular/core';
import { ApiService } from '@services';
import { Observable } from 'rxjs';

export interface CalendarEvent {
  id: string;
  userId: string;
  title: string;
  description?: string;
  startAt: string;
  endAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class CalendarService {
  private readonly api = inject(ApiService);

  getCalendarEvents(start: string, end: string): Observable<{ calendarEvents: CalendarEvent[] }> {
    const query = `
      query GetCalendarEvents($start: String!, $end: String!) {
        calendarEvents(start: $start, end: $end) {
          id
          userId
          title
          description
          startAt
          endAt
        }
      }
    `;
    return this.api.graphql<{ calendarEvents: CalendarEvent[] }>(query, { start, end });
  }

  createCalendarEvent(title: string, description: string, start: string, end: string): Observable<{ createCalendarEvent: CalendarEvent }> {
    const mutation = `
      mutation CreateCalendarEvent($title: String!, $description: String, $start: String!, $end: String!) {
        createCalendarEvent(title: $title, description: $description, start: $start, end: $end) {
          id
          userId
          title
          description
          startAt
          endAt
        }
      }
    `;
    return this.api.graphql<{ createCalendarEvent: CalendarEvent }>(mutation, { title, description, start, end });
  }
}
