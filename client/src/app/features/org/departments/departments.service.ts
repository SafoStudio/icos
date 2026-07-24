import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '@services';
import { type Department, type Permission, type User, type DepartmentMember } from '@models';

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

  getDepartment(id: string): Observable<Department> {
    const query = `
      query GetDepartment($id: ID!) {
        department(id: $id) {
          id
          name
          description
        }
      }
    `;
    return this.api.graphql<{ department: Department }>(query, { id }).pipe(
      map(res => res.department)
    );
  }

  getDepartmentHead(departmentId: string): Observable<User> {
    const query = `
      query GetDepartmentHead($departmentId: ID!) {
        departmentHead(departmentId: $departmentId) {
          id
          firstName
          lastName
          email
        }
      }
    `;
    return this.api.graphql<{ departmentHead: User }>(query, { departmentId }).pipe(
      map(res => res.departmentHead)
    );
  }

  getDepartmentMembers(departmentId: string): Observable<DepartmentMember[]> {
    const query = `
      query GetDepartmentMembers($departmentId: ID!) {
        departmentMembers(departmentId: $departmentId) {
          userId
          firstName
          lastName
          positionName
        }
      }
    `;
    return this.api.graphql<{ departmentMembers: DepartmentMember[] }>(query, { departmentId }).pipe(
      map(res => res.departmentMembers)
    );
  }

  getAllPermissions(): Observable<Permission[]> {
    const query = `
      query GetAllPermissions {
        permissions {
          id
          name
          description
        }
      }
    `;
    return this.api.graphql<{ permissions: Permission[] }>(query).pipe(
      map(res => res.permissions)
    );
  }

  getDepartmentPermissions(departmentId: string, roleName: string): Observable<Permission[]> {
    const query = `
      query GetDepartmentPermissions($departmentId: ID!, $roleName: String!) {
        departmentPermissions(departmentId: $departmentId, roleName: $roleName) {
          id
          name
        }
      }
    `;
    return this.api.graphql<{ departmentPermissions: Permission[] }>(query, { departmentId, roleName }).pipe(
      map(res => res.departmentPermissions)
    );
  }

  assignDepartmentPermission(departmentId: string, roleName: string, permissionId: string): Observable<boolean> {
    const mutation = `
      mutation AssignDepartmentPermission($departmentId: ID!, $roleName: String!, $permissionId: ID!) {
        assignDepartmentPermission(departmentId: $departmentId, roleName: $roleName, permissionId: $permissionId)
      }
    `;
    return this.api.graphql<{ assignDepartmentPermission: boolean }>(mutation, { departmentId, roleName, permissionId }).pipe(
      map(res => res.assignDepartmentPermission)
    );
  }

  revokeDepartmentPermission(departmentId: string, roleName: string, permissionId: string): Observable<boolean> {
    const mutation = `
      mutation RevokeDepartmentPermission($departmentId: ID!, $roleName: String!, $permissionId: ID!) {
        revokeDepartmentPermission(departmentId: $departmentId, roleName: $roleName, permissionId: $permissionId)
      }
    `;
    return this.api.graphql<{ revokeDepartmentPermission: boolean }>(mutation, { departmentId, roleName, permissionId }).pipe(
      map(res => res.revokeDepartmentPermission)
    );
  }
}
