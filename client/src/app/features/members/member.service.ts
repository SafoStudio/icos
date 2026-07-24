import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '@services';
import { type Department } from '@models';

export interface Member {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  departmentId?: string;
  departmentName?: string;
  status: string;
}

export interface InviteInput {
  email: string;
  role: string;
  departmentId: string;
  managerId: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class MemberService {
  private readonly api = inject(ApiService);

  getDepartments(companyId: string): Observable<Department[]> {
    const query = `
      query GetDepartments($companyId: ID!) {
        departments(companyId: $companyId) {
          id
          name
          description
        }
      }
    `;
    return this.api.graphql<{ departments: Department[] }>(query, { companyId }).pipe(
      map(res => res.departments || [])
    );
  }

  getMembers(companyId: string): Observable<Member[]> {
    const query = `
      query UsersByCompany($companyId: ID!) {
        usersByCompany(companyId: $companyId) {
          id
          firstName
          lastName
          email
          role
          departmentId
          status
        }
      }
    `;
    return this.api.graphql<{ usersByCompany: Member[] }>(query, { companyId }).pipe(
      map(res => res.usersByCompany || [])
    );
  }

  createInvite(input: InviteInput): Observable<any> {
    const mutation = `
      mutation CreateInvite($input: InviteRequestInput!) {
        createInvite(input: $input) {
          id
          email
          role
          status
        }
      }
    `;
    return this.api.graphql<{ createInvite: any }>(mutation, { input }).pipe(
      map(res => res.createInvite)
    );
  }
}
