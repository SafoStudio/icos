import { Injectable, inject } from '@angular/core';
import { ApiService } from '@services';
import { DepartmentsService } from '../org/departments/departments.service';
import { type DepartmentMember } from '@models';
import { Observable } from 'rxjs';

export interface TaskItem {
  id: string;
  title: string;
  description?: string;
  status: 'TODO' | 'IN_PROGRESS' | 'REVIEW' | 'DONE';
  assigneeId: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class TaskBoardService {
  private readonly api = inject(ApiService);
  private readonly departmentsService = inject(DepartmentsService);

  getDepartmentMembers(departmentId: string): Observable<DepartmentMember[]> {
    return this.departmentsService.getDepartmentMembers(departmentId);
  }

  getTasksByAssignee(assigneeId: string): Observable<{ tasksByAssignee: TaskItem[] }> {
    const query = `
      query GetTasksByAssignee($assigneeId: ID!) {
        tasksByAssignee(assigneeId: $assigneeId) {
          id
          title
          description
          status
          assigneeId
        }
      }
    `;
    return this.api.graphql<{ tasksByAssignee: TaskItem[] }>(query, { assigneeId });
  }

  getTasksByDepartment(departmentId: string): Observable<{ tasksByDepartment: TaskItem[] }> {
    const query = `
      query GetTasksByDepartment($departmentId: ID!) {
        tasksByDepartment(departmentId: $departmentId) {
          id
          title
          description
          status
          assigneeId
        }
      }
    `;
    return this.api.graphql<{ tasksByDepartment: TaskItem[] }>(query, { departmentId });
  }

  createTask(
    title: string,
    description: string,
    assigneeId: string | null,
    departmentId: string | null
  ): Observable<{ createTask: TaskItem }> {
    const mutation = `
      mutation CreateTask($title: String!, $description: String, $assigneeId: ID, $departmentId: ID) {
        createTask(title: $title, description: $description, assigneeId: $assigneeId, departmentId: $departmentId) {
          id
          title
          description
          status
          assigneeId
        }
      }
    `;
    return this.api.graphql<{ createTask: TaskItem }>(
      mutation,
      { title, description, assigneeId, departmentId }
    );
  }

  moveTask(taskId: string, newStatus: string): Observable<{ moveTask: TaskItem }> {
    const mutation = `
      mutation MoveTask($taskId: ID!, $newStatus: String!) {
        moveTask(taskId: $taskId, newStatus: $newStatus) {
          id
          title
          description
          status
          assigneeId
        }
      }
    `;
    return this.api.graphql<{ moveTask: TaskItem }>(mutation, { taskId, newStatus });
  }
}
