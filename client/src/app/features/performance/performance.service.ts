import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from '@services';
import { Objective, PerformanceReview, CreateObjectiveInput, SubmitReviewInput } from '@models';

@Injectable({
  providedIn: 'root'
})
export class PerformanceService {
  private readonly api = inject(ApiService);

  getMyObjectives(): Observable<Objective[]> {
    const query = `
      query GetMyObjectives {
        myObjectives {
          id
          title
          description
          startDate
          endDate
          progress
          status
        }
      }
    `;
    return this.api.graphql<{ myObjectives: Objective[] }>(query).pipe(
      map(res => res.myObjectives || [])
    );
  }

  getUserObjectives(userId: string): Observable<Objective[]> {
    const query = `
      query GetUserObjectives($userId: ID!) {
        userObjectives(userId: $userId) {
          id
          title
          description
          startDate
          endDate
          progress
          status
        }
      }
    `;
    return this.api.graphql<{ userObjectives: Objective[] }>(query, { userId }).pipe(
      map(res => res.userObjectives || [])
    );
  }

  createObjective(input: CreateObjectiveInput): Observable<Objective> {
    if (input.userId) {
      const mutation = `
        mutation CreateUserObjective($userId: ID!, $title: String!, $description: String, $startDate: String!, $endDate: String!) {
          createUserObjective(userId: $userId, title: $title, description: $description, startDate: $startDate, endDate: $endDate) {
            id
            title
            description
            startDate
            endDate
            progress
            status
          }
        }
      `;
      return this.api.graphql<{ createUserObjective: Objective }>(mutation, input).pipe(
        map(res => res.createUserObjective)
      );
    }

    const mutation = `
      mutation CreateObjective($title: String!, $description: String, $startDate: String!, $endDate: String!) {
        createObjective(title: $title, description: $description, startDate: $startDate, endDate: $endDate) {
          id
          title
          description
          startDate
          endDate
          progress
          status
        }
      }
    `;
    return this.api.graphql<{ createObjective: Objective }>(mutation, {
      title: input.title,
      description: input.description,
      startDate: input.startDate,
      endDate: input.endDate
    }).pipe(
      map(res => res.createObjective)
    );
  }

  getEmployeeReviews(employeeId: string): Observable<PerformanceReview[]> {
    const query = `
      query GetEmployeeReviews($employeeId: ID!) {
        employeeReviews(employeeId: $employeeId) {
          id
          employeeId
          rating
          summary
          status
          finalizedAt
        }
      }
    `;
    return this.api.graphql<{ employeeReviews: PerformanceReview[] }>(query, { employeeId }).pipe(
      map(res => res.employeeReviews || [])
    );
  }

  submitReview(input: SubmitReviewInput): Observable<PerformanceReview> {
    const mutation = `
      mutation SubmitReview($employeeId: ID!, $rating: Int!, $summary: String!) {
        submitReview(employeeId: $employeeId, rating: $rating, summary: $summary) {
          id
          employeeId
          rating
          summary
          status
          finalizedAt
        }
      }
    `;
    return this.api.graphql<{ submitReview: PerformanceReview }>(mutation, input).pipe(
      map(res => res.submitReview)
    );
  }

  finalizeReview(id: string, rating: number, summary: string): Observable<PerformanceReview> {
    const mutation = `
      mutation FinalizeReview($id: ID!, $rating: Int!, $summary: String!) {
        finalizeReview(id: $id, rating: $rating, summary: $summary) {
          id
          employeeId
          rating
          summary
          status
          finalizedAt
        }
      }
    `;
    return this.api.graphql<{ finalizeReview: PerformanceReview }>(mutation, { id, rating, summary }).pipe(
      map(res => res.finalizeReview)
    );
  }
}
