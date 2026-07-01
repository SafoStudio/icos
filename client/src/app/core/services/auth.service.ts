import { Injectable, inject, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap, of, catchError } from 'rxjs';
import { ApiService } from './api.service';

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  companyId: string;
  role: string;
  departmentId?: string;
  permissions: string[];
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface InviteResponse {
  id: string;
  email: string;
  role: string;
  companyId: string;
  companyName: string;
  departmentId: string;
  departmentName: string;
  managerId?: string;
  managerName?: string;
  status: string;
  expiresAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private refreshTimeout: any;

  readonly currentUser = signal<User | null>(null);
  readonly token = signal<string | null>(null);
  readonly isLoggedIn = computed(() => this.currentUser() !== null);

  constructor() {
    this.loadSession();
  }

  private loadSession(): void {
    const savedUser = localStorage.getItem('current_user');
    if (savedUser) {
      this.currentUser.set(JSON.parse(savedUser));
    }
  }

  private saveSession(authData: AuthResponse): void {
    localStorage.setItem('current_user', JSON.stringify(authData.user));
    this.token.set(authData.accessToken);
    this.currentUser.set(authData.user);
    this.scheduleRefresh(authData.expiresIn);
  }

  private clearSession(): void {
    localStorage.removeItem('current_user');
    this.token.set(null);
    this.currentUser.set(null);
    this.clearScheduledRefresh();
  }

  getAccessToken(): string | null {
    return this.token();
  }

  login(email: string, password: string): Observable<any> {
    const mutation = `
      mutation Login($email: String!, $password: String!) {
        login(email: $email, password: $password) {
          accessToken
          tokenType
          expiresIn
          user {
            id
            email
            firstName
            lastName
            companyId
            role
            departmentId
            permissions
          }
        }
      }
    `;

    return this.api.graphql<{ login: AuthResponse }>(mutation, { email, password }).pipe(
      tap(data => this.saveSession(data.login))
    );
  }

  registerCompany(email: string, password: string, firstName: string, lastName: string, companyName: string): Observable<any> {
    const mutation = `
      mutation Register($email: String!, $password: String!, $firstName: String!, $lastName: String!, $companyName: String!) {
        register(email: $email, password: $password, firstName: $firstName, lastName: $lastName, companyName: $companyName) {
          accessToken
          tokenType
          expiresIn
          user {
            id
            email
            firstName
            lastName
            companyId
            role
            departmentId
            permissions
          }
        }
      }
    `;

    return this.api.graphql<{ register: AuthResponse }>(mutation, {
      email,
      password,
      firstName,
      lastName,
      companyName
    }).pipe(
      tap(data => this.saveSession(data.register))
    );
  }

  getInviteInfo(token: string): Observable<InviteResponse> {
    return this.api.get<InviteResponse>(`/api/public/invites/${token}`);
  }

  acceptInvite(token: string, body: any): Observable<AuthResponse> {
    return this.api.post<AuthResponse>(`/api/public/invites/${token}/accept`, body).pipe(
      tap(data => this.saveSession(data))
    );
  }

  refreshToken(): Observable<any> {
    const mutation = `
      mutation Refresh($refreshToken: String!) {
        refresh(refreshToken: $refreshToken) {
          accessToken
          expiresIn
        }
      }
    `;

    return this.api.graphql<{ refresh: { accessToken: string; expiresIn: number } }>(mutation, { refreshToken: '' }).pipe(
      tap(data => {
        this.token.set(data.refresh.accessToken);
        this.scheduleRefresh(data.refresh.expiresIn);
      })
    );
  }

  checkAuth(): Observable<any> {
    const savedUser = localStorage.getItem('current_user');
    if (savedUser) {
      return this.refreshToken().pipe(
        tap({
          next: () => {
            this.currentUser.set(JSON.parse(savedUser));
          },
          error: () => {
            this.clearSession();
          }
        }),
        catchError(() => {
          this.clearSession();
          return of(null);
        })
      );
    }
    this.clearSession();
    return of(null);
  }

  private scheduleRefresh(expiresInSeconds: number): void {
    this.clearScheduledRefresh();
    const delay = Math.max((expiresInSeconds - 60) * 1000, 1000);
    this.refreshTimeout = setTimeout(() => {
      this.refreshToken().subscribe({
        error: () => this.clearSession()
      });
    }, delay);
  }

  private clearScheduledRefresh(): void {
    if (this.refreshTimeout) {
      clearTimeout(this.refreshTimeout);
      this.refreshTimeout = undefined;
    }
  }

  logout(): void {
    const mutation = `
      mutation Logout($refreshToken: String!) {
        logout(refreshToken: $refreshToken)
      }
    `;
    this.api.graphql<boolean>(mutation, { refreshToken: '' }).subscribe({
      next: () => {
        this.clearSession();
        this.router.navigate(['/login']);
      },
      error: () => {
        this.clearSession();
        this.router.navigate(['/login']);
      }
    });
  }
}
