import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '@services';
import { type Position } from '@models';

@Injectable({
  providedIn: 'root'
})
export class PositionsService {
  private readonly api = inject(ApiService);

  getPositions(companyId: string): Observable<Position[]> {
    const query = `
      query GetPositions($companyId: ID!) {
        positions(companyId: $companyId) {
          id
          name
          description
        }
      }
    `;

    return this.api.graphql<{ positions: Position[] }>(query, { companyId }).pipe(
      map(response => response.positions)
    );
  }

  createPosition(companyId: string, name: string, description: string): Observable<Position> {
    const mutation = `
      mutation CreatePosition($name: String!, $description: String) {
        createPosition(name: $name, description: $description) {
          id
          name
          description
        }
      }
    `;

    return this.api.graphql<{ createPosition: Position }>(mutation, { name, description }).pipe(
      map(response => response.createPosition)
    );
  }

  updatePosition(id: string, name: string, description: string): Observable<Position> {
    const mutation = `
      mutation UpdatePosition($id: ID!, $name: String!, $description: String) {
        updatePosition(id: $id, name: $name, description: $description) {
          id
          name
          description
        }
      }
    `;

    return this.api.graphql<{ updatePosition: Position }>(mutation, { id, name, description }).pipe(
      map(response => response.updatePosition)
    );
  }

  deletePosition(id: string): Observable<boolean> {
    const mutation = `
      mutation DeletePosition($id: ID!) {
        deletePosition(id: $id)
      }
    `;

    return this.api.graphql<{ deletePosition: boolean }>(mutation, { id }).pipe(
      map(response => response.deletePosition)
    );
  }
}
