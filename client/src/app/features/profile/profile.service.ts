import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../../core/services/api.service';
import { User } from '../../shared/models/user.model';
import { UserProfile } from '../../shared/models/user-profile.model';

export interface ProfileUpdateInput {
  userId: string;
  phoneNumber?: string;
  address?: string;
  hireDate?: string;
  birthDate?: string;
}

export interface UserUpdateInput {
  firstName?: string;
  lastName?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private readonly api = inject(ApiService);

  getMe(): Observable<User> {
    const query = `
      query GetMe {
        me {
          id
          email
          firstName
          lastName
          companyId
          role
          departmentId
          permissions
          profile {
            userId
            phoneNumber
            address
            hireDate
            birthDate
          }
        }
      }
    `;
    return this.api.graphql<{ me: User }>(query).pipe(
      map((res: any) => res.me)
    );
  }

  updateProfile(input: ProfileUpdateInput): Observable<UserProfile> {
    const mutation = `
      mutation UpdateProfile($userId: ID!, $input: ProfileUpdateInput!) {
        updateProfile(userId: $userId, input: $input) {
          userId
          phoneNumber
          address
          hireDate
          birthDate
        }
      }
    `;
    return this.api.graphql<{ updateProfile: UserProfile }>(mutation, {
      userId: input.userId,
      input
    }).pipe(
      map((res: any) => res.updateProfile)
    );
  }

  updateUser(id: string, input: UserUpdateInput): Observable<User> {
    const mutation = `
      mutation UpdateUser($id: ID!, $input: UserUpdateInput!) {
        updateUser(id: $id, input: $input) {
          id
          email
          firstName
          lastName
          companyId
          role
          departmentId
          permissions
          profile {
            userId
            phoneNumber
            address
            hireDate
            birthDate
          }
        }
      }
    `;
    return this.api.graphql<{ updateUser: User }>(mutation, { id, input }).pipe(
      map((res: any) => res.updateUser)
    );
  }
}
