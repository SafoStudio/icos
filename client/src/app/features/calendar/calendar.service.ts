import { Injectable, inject } from '@angular/core';
import { ApiService } from '@services';
import { Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export interface CalendarParticipant {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role?: string;
  profile?: {
    avatarPath?: string;
    phoneNumber?: string;
    address?: string;
  };
}

export interface EventParticipant {
  userId: string;
  status: 'PENDING' | 'ACCEPTED' | 'DECLINED';
  user?: CalendarParticipant;
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
  participantDetails?: EventParticipant[];
  organizer?: CalendarParticipant;
  currentUserStatus?: 'PENDING' | 'ACCEPTED' | 'DECLINED';
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
          currentUserStatus
          participantDetails {
            userId
            status
            user {
              id
              firstName
              lastName
              email
              role
              profile {
                avatarPath
              }
            }
          }
          participants {
            id
            firstName
            lastName
            email
            role
            profile {
              avatarPath
            }
          }
          organizer {
            id
            firstName
            lastName
            email
            role
            profile {
              avatarPath
            }
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
          currentUserStatus
          participantDetails {
            userId
            status
            user {
              id
              firstName
              lastName
              email
              role
              profile {
                avatarPath
              }
            }
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

  updateCalendarEvent(
    id: string,
    title: string,
    description: string | null,
    start: string,
    end: string,
    participantIds: string[] = []
  ): Observable<{ updateCalendarEvent: CalendarEvent }> {
    const mutation = `
      mutation UpdateCalendarEvent(
        $id: ID!,
        $title: String!,
        $description: String,
        $start: String!,
        $end: String!,
        $participantIds: [ID!]
      ) {
        updateCalendarEvent(
          id: $id,
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
          currentUserStatus
          participantDetails {
            userId
            status
            user {
              id
              firstName
              lastName
              email
              role
              profile {
                avatarPath
              }
            }
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
    return this.api.graphql<{ updateCalendarEvent: CalendarEvent }>(mutation, {
      id,
      title,
      description,
      start,
      end,
      participantIds
    });
  }

  updateEventAttendance(
    eventId: string,
    status: 'ACCEPTED' | 'DECLINED' | 'PENDING'
  ): Observable<{ updateEventAttendance: CalendarEvent }> {
    const mutation = `
      mutation UpdateEventAttendance($eventId: ID!, $status: String!) {
        updateEventAttendance(eventId: $eventId, status: $status) {
          id
          userId
          title
          description
          startAt
          endAt
          participantIds
          currentUserStatus
          participantDetails {
            userId
            status
            user {
              id
              firstName
              lastName
              email
              role
              profile {
                avatarPath
              }
            }
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
    return this.api.graphql<{ updateEventAttendance: CalendarEvent }>(mutation, {
      eventId,
      status
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

  getCompanyMembers(companyId: string): Observable<CalendarParticipant[]> {
    const query = `
      query UsersByCompany($companyId: ID!) {
        usersByCompany(companyId: $companyId) {
          id
          firstName
          lastName
          email
          role
          profile {
            avatarPath
          }
        }
      }
    `;
    return this.api.graphql<{ usersByCompany: CalendarParticipant[] }>(query, { companyId }).pipe(
      map(res => res.usersByCompany || []),
      catchError(error => {
        console.error('Error fetching company members:', error);
        return [];
      })
    );
  }
}
