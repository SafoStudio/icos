import { Injectable, inject } from '@angular/core';
import { ApiService } from '@services';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface CalendarParticipant {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role?: string;
}

export interface CalendarEvent {
  id: string;
  userId: string;
  title: string;
  description?: string;
  startAt: string;
  endAt: string;
  type?: string;
  source?: string;
  visibility?: string;
  participantIds?: string[];
  participants?: CalendarParticipant[];
  organizer?: CalendarParticipant;
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
          type
          source
          visibility
          participantIds
          participants {
            id
            firstName
            lastName
            email
            role
          }
          organizer {
            id
            firstName
            lastName
            email
            role
          }
        }
      }
    `;
    return this.api.graphql<{ calendarEvents: CalendarEvent[] }>(query, { start, end })
      .pipe(
        catchError(error => {
          console.error('Error fetching calendar events:', error);
          throw error;
        })
      );
  }

  createCalendarEvent(
    title: string,
    description: string | null,
    start: string,
    end: string,
    participantIds: string[] = []
  ): Observable<{ createCalendarEvent: CalendarEvent }> {
    const mutation = `
      mutation CreateCalendarEvent(
        $title: String!,
        $description: String,
        $start: String!,
        $end: String!,
        $participantIds: [ID!]
      ) {
        createCalendarEvent(
          title: $title,
          description: $description,
          start: $start,
          end: $end,
          participantIds: $participantIds
        ) {
          id
          userId
          title
          description
          startAt
          endAt
          participantIds
          participants {
            id
            firstName
            lastName
            email
            role
          }
          organizer {
            id
            firstName
            lastName
            email
            role
          }
        }
      }
    `;
    return this.api.graphql<{ createCalendarEvent: CalendarEvent }>(mutation, {
      title,
      description,
      start,
      end,
      participantIds
    });
  }

  deleteCalendarEvent(id: string): Observable<{ deleteCalendarEvent: boolean }> {
    const mutation = `
      mutation DeleteCalendarEvent($id: ID!) {
        deleteCalendarEvent(id: $id)
      }
    `;
    return this.api.graphql<{ deleteCalendarEvent: boolean }>(mutation, { id });
  }
}
