import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { Position } from '../../../shared/models/position.model';

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
      mutation CreatePosition($companyId: ID!, $name: String!, $description: String) {
        createPosition(companyId: $companyId, name: $name, description: $description) {
          id
          name
          description
        }
      }
    `;

    return this.api.graphql<{ createPosition: Position }>(mutation, { companyId, name, description }).pipe(
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
