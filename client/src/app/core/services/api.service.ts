import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly http = inject(HttpClient);

  get<T>(url: string): Observable<T> {
    return this.http.get<T>(url);
  }

  post<T>(url: string, body: any): Observable<T> {
    return this.http.post<T>(url, body);
  }

  delete<T>(url: string): Observable<T> {
    return this.http.delete<T>(url);
  }

  graphql<T>(query: string, variables: Record<string, any> = {}): Observable<T> {
    return this.http.post<{ data: T; errors?: any[] }>('/graphql', { query, variables }).pipe(
      map(response => {
        if (response.errors && response.errors.length > 0) {
          throw new Error(response.errors[0].message || 'GraphQL Error');
        }
        return response.data;
      })
    );
  }
}
