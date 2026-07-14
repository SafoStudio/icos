import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';
import { Department } from '../../../shared/models/department.model';

@Injectable({
  providedIn: 'root'
})
export class DepartmentsService {
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
      map(response => response.departments)
    );
  }

  createDepartment(companyId: string, name: string, description: string): Observable<Department> {
    const mutation = `
      mutation CreateDepartment($name: String!, $description: String) {
        createDepartment(name: $name, description: $description) {
          id
          name
          description
        }
      }
    `;

    return this.api.graphql<{ createDepartment: Department }>(mutation, { name, description }).pipe(
      map(response => response.createDepartment)
    );
  }

  updateDepartment(id: string, name: string, description: string): Observable<Department> {
    const mutation = `
      mutation UpdateDepartment($id: ID!, $name: String!, $description: String) {
        updateDepartment(id: $id, name: $name, description: $description) {
          id
          name
          description
        }
      }
    `;

    return this.api.graphql<{ updateDepartment: Department }>(mutation, { id, name, description }).pipe(
      map(response => response.updateDepartment)
    );
  }

  deleteDepartment(id: string): Observable<boolean> {
    const mutation = `
      mutation DeleteDepartment($id: ID!) {
        deleteDepartment(id: $id)
      }
    `;

    return this.api.graphql<{ deleteDepartment: boolean }>(mutation, { id }).pipe(
      map(response => response.deleteDepartment)
    );
  }
}
