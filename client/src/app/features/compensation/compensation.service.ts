import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from '@services';
import { Compensation, SalaryRevision, UpdateSalaryInput } from '@models';

@Injectable({
  providedIn: 'root'
})
export class CompensationService {
  private readonly api = inject(ApiService);

  getCompensation(userId: string): Observable<Compensation | null> {
    const query = `
      query GetCompensation($userId: ID!) {
        compensation(userId: $userId) {
          id
          userId
          baseSalary
          currency
          payFrequency
          effectiveDate
        }
      }
    `;
    return this.api.graphql<{ compensation: Compensation | null }>(query, { userId }).pipe(
      map(res => res.compensation)
    );
  }

  getSalaryHistory(userId: string): Observable<SalaryRevision[]> {
    const query = `
      query GetSalaryHistory($userId: ID!) {
        salaryHistory(userId: $userId) {
          id
          userId
          previousSalary
          newSalary
          reason
          effectiveDate
        }
      }
    `;
    return this.api.graphql<{ salaryHistory: SalaryRevision[] }>(query, { userId }).pipe(
      map(res => res.salaryHistory || [])
    );
  }

  updateSalary(input: UpdateSalaryInput): Observable<Compensation> {
    const mutation = `
      mutation UpdateSalary($userId: ID!, $newSalary: Float!, $reason: String!, $effectiveDate: String!) {
        updateSalary(userId: $userId, newSalary: $newSalary, reason: $reason, effectiveDate: $effectiveDate) {
          id
          userId
          baseSalary
          currency
          payFrequency
          effectiveDate
        }
      }
    `;
    return this.api.graphql<{ updateSalary: Compensation }>(mutation, {
      userId: input.userId,
      newSalary: input.newSalary,
      reason: input.reason,
      effectiveDate: input.effectiveDate
    }).pipe(
      map(res => res.updateSalary)
    );
  }
}
