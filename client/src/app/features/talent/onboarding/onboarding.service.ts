import { Injectable, inject } from '@angular/core';
import { ApiService } from '@services';
import { Observable } from 'rxjs';

export interface OnboardingTask {
  id: string;
  processId: string;
  title: string;
  description?: string;
  assigneeId: string;
  status: 'PENDING' | 'DONE' | 'WAITING';
  dueDate?: string;
  completedAt?: string;
}

export type OnboardingProcessStatus = 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'STALLED';

export interface OnboardingProcess {
  id: string;
  membershipId: string;
  templateId?: string;
  status: OnboardingProcessStatus;
  startDate: string;
  targetCompletionDate?: string;
  tasks: OnboardingTask[];
}

export interface OnboardingTemplate {
  id: string;
  companyId: string;
  name: string;
  description?: string;
}

export interface StartOnboardingInput {
  membershipId: string;
  templateId: string;
  startDate: string;
}

@Injectable({
  providedIn: 'root'
})
export class OnboardingService {
  private readonly api = inject(ApiService);

  getProcessesByCompany(companyId: string): Observable<{ onboardingProcessesByCompany: OnboardingProcess[] }> {
    const query = `
      query GetOnboardingProcesses($companyId: UUID!) {
        onboardingProcessesByCompany(companyId: $companyId) {
          id
          membershipId
          templateId
          status
          startDate
          targetCompletionDate
          tasks {
            id
            processId
            title
            description
            assigneeId
            status
            dueDate
            completedAt
          }
        }
      }
    `;
    return this.api.graphql<{ onboardingProcessesByCompany: OnboardingProcess[] }>(query, { companyId });
  }

  getTemplates(companyId: string): Observable<{ onboardingTemplates: OnboardingTemplate[] }> {
    const query = `
      query GetOnboardingTemplates($companyId: UUID!) {
        onboardingTemplates(companyId: $companyId) {
          id
          companyId
          name
          description
        }
      }
    `;
    return this.api.graphql<{ onboardingTemplates: OnboardingTemplate[] }>(query, { companyId });
  }

  startOnboarding(input: StartOnboardingInput): Observable<{ startOnboarding: OnboardingProcess }> {
    const mutation = `
      mutation StartOnboarding($input: StartOnboardingInput!) {
        startOnboarding(input: $input) {
          id
          membershipId
          status
          startDate
        }
      }
    `;
    return this.api.graphql<{ startOnboarding: OnboardingProcess }>(mutation, { input });
  }

  completeTask(taskId: string): Observable<{ completeOnboardingTask: OnboardingTask }> {
    const mutation = `
      mutation CompleteOnboardingTask($taskId: UUID!) {
        completeOnboardingTask(taskId: $taskId) {
          id
          status
          completedAt
        }
      }
    `;
    return this.api.graphql<{ completeOnboardingTask: OnboardingTask }>(mutation, { taskId });
  }
}
