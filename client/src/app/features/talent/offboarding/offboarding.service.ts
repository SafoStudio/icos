import { Injectable, inject } from '@angular/core';
import { ApiService } from '@services';
import { Observable } from 'rxjs';

export interface OffboardingTask {
  id: string;
  processId: string;
  title: string;
  description?: string;
  assigneeId: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'DONE' | 'SKIPPED' | 'BLOCKED';
  dueDate?: string;
  completedAt?: string;
}

export type OffboardingProcessStatus =
  | 'PENDING_APPROVAL'
  | 'HR_INTERVIEW'
  | 'KNOWLEDGE_TRANSFER'
  | 'IT_REVOKE'
  | 'FINANCIAL_SETTLEMENT'
  | 'READY_FOR_SIGN_OFF'
  | 'COMPLETED'
  | 'CANCELLED';

export interface OffboardingProcess {
  id: string;
  membershipId: string;
  templateId?: string;
  status: OffboardingProcessStatus;
  startDate: string;
  lastWorkingDay: string;
  reason?: string;
  initiatedBy?: string;
  tasks: OffboardingTask[];
}

export interface OffboardingTemplate {
  id: string;
  companyId: string;
  name: string;
  description?: string;
}

export interface StartOffboardingInput {
  membershipId: string;
  templateId: string;
  lastWorkingDay: string;
  reason: string;
  initiatedBy: string;
}

export interface TransitionOffboardingProcessInput {
  processId: string;
  status: OffboardingProcessStatus;
}

@Injectable({
  providedIn: 'root'
})
export class OffboardingService {
  private readonly api = inject(ApiService);

  getProcessesByCompany(companyId: string): Observable<{ offboardingProcessesByCompany: OffboardingProcess[] }> {
    const query = `
      query GetOffboardingProcesses($companyId: UUID!) {
        offboardingProcessesByCompany(companyId: $companyId) {
          id
          membershipId
          templateId
          status
          startDate
          lastWorkingDay
          reason
          initiatedBy
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
    return this.api.graphql<{ offboardingProcessesByCompany: OffboardingProcess[] }>(query, { companyId });
  }

  getTemplates(companyId: string): Observable<{ offboardingTemplates: OffboardingTemplate[] }> {
    const query = `
      query GetOffboardingTemplates($companyId: UUID!) {
        offboardingTemplates(companyId: $companyId) {
          id
          companyId
          name
          description
        }
      }
    `;
    return this.api.graphql<{ offboardingTemplates: OffboardingTemplate[] }>(query, { companyId });
  }

  startOffboarding(input: StartOffboardingInput): Observable<{ startOffboarding: OffboardingProcess }> {
    const mutation = `
      mutation StartOffboarding($input: StartOffboardingInput!) {
        startOffboarding(input: $input) {
          id
          membershipId
          status
          startDate
          lastWorkingDay
          reason
        }
      }
    `;
    return this.api.graphql<{ startOffboarding: OffboardingProcess }>(mutation, { input });
  }

  approveProcess(processId: string): Observable<{ approveOffboardingProcess: OffboardingProcess }> {
    const mutation = `
      mutation ApproveOffboardingProcess($processId: UUID!) {
        approveOffboardingProcess(processId: $processId) {
          id
          status
        }
      }
    `;
    return this.api.graphql<{ approveOffboardingProcess: OffboardingProcess }>(mutation, { processId });
  }

  transitionProcess(input: TransitionOffboardingProcessInput): Observable<{ transitionOffboardingProcess: OffboardingProcess }> {
    const mutation = `
      mutation TransitionOffboardingProcess($input: TransitionOffboardingProcessInput!) {
        transitionOffboardingProcess(input: $input) {
          id
          status
        }
      }
    `;
    return this.api.graphql<{ transitionOffboardingProcess: OffboardingProcess }>(mutation, { input });
  }

  signOffProcess(processId: string): Observable<{ signOffOffboardingProcess: OffboardingProcess }> {
    const mutation = `
      mutation SignOffOffboardingProcess($processId: UUID!) {
        signOffOffboardingProcess(processId: $processId) {
          id
          status
        }
      }
    `;
    return this.api.graphql<{ signOffOffboardingProcess: OffboardingProcess }>(mutation, { processId });
  }

  cancelProcess(processId: string): Observable<{ cancelOffboardingProcess: boolean }> {
    const mutation = `
      mutation CancelOffboardingProcess($processId: UUID!) {
        cancelOffboardingProcess(processId: $processId)
      }
    `;
    return this.api.graphql<{ cancelOffboardingProcess: boolean }>(mutation, { processId });
  }

  startTask(taskId: string): Observable<{ startOffboardingTask: OffboardingTask }> {
    const mutation = `
      mutation StartOffboardingTask($taskId: UUID!) {
        startOffboardingTask(taskId: $taskId) {
          id
          status
        }
      }
    `;
    return this.api.graphql<{ startOffboardingTask: OffboardingTask }>(mutation, { taskId });
  }

  completeTask(taskId: string): Observable<{ completeOffboardingTask: OffboardingTask }> {
    const mutation = `
      mutation CompleteOffboardingTask($taskId: UUID!) {
        completeOffboardingTask(taskId: $taskId) {
          id
          status
          completedAt
        }
      }
    `;
    return this.api.graphql<{ completeOffboardingTask: OffboardingTask }>(mutation, { taskId });
  }

  skipTask(taskId: string): Observable<{ skipOffboardingTask: OffboardingTask }> {
    const mutation = `
      mutation SkipOffboardingTask($taskId: UUID!) {
        skipOffboardingTask(taskId: $taskId) {
          id
          status
        }
      }
    `;
    return this.api.graphql<{ skipOffboardingTask: OffboardingTask }>(mutation, { taskId });
  }
}
