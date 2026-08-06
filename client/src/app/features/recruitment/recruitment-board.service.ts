import { Injectable, inject } from '@angular/core';
import { ApiService } from '@services';
import { Observable } from 'rxjs';

export interface Vacancy {
  id: string;
  companyId: string;
  departmentId?: string;
  positionId?: string;
  title: string;
  description?: string;
  requirements?: string;
  status: 'DRAFT' | 'OPEN' | 'CLOSED' | 'PAUSED';
  createdAt: string;
}

export interface Candidate {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  source?: string;
  resumeUrl?: string;
}

export interface Application {
  id: string;
  candidate: Candidate;
  vacancyId: string;
  currentStageId?: string;
  status: 'ACTIVE' | 'HIRED' | 'REJECTED' | 'WITHDRAWN';
  createdAt: string;
}

export interface VacancyStage {
  id: string;
  name: string;
  type: string;
  orderIndex: number;
}

export interface KanbanColumn {
  stage: VacancyStage;
  applications: Application[];
}

export interface KanbanBoard {
  vacancyId: string;
  columns: KanbanColumn[];
}

export interface CreateVacancyInput {
  companyId: string;
  departmentId?: string;
  positionId?: string;
  title: string;
  description?: string;
  requirements?: string;
}

export interface CandidateInput {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  source?: string;
  resumeUrl?: string;
}

@Injectable({
  providedIn: 'root'
})
export class RecruitmentBoardService {
  private readonly api = inject(ApiService);

  getVacancies(companyId: string): Observable<{ vacancies: Vacancy[] }> {
    const query = `
      query GetVacancies($companyId: UUID!) {
        vacancies(companyId: $companyId) {
          id
          companyId
          departmentId
          positionId
          title
          description
          requirements
          status
          createdAt
        }
      }
    `;
    return this.api.graphql<{ vacancies: Vacancy[] }>(query, { companyId });
  }

  getKanbanBoard(vacancyId: string): Observable<{ kanbanBoard: KanbanBoard }> {
    const query = `
      query GetKanbanBoard($vacancyId: UUID!) {
        kanbanBoard(vacancyId: $vacancyId) {
          vacancyId
          columns {
            stage {
              id
              name
              type
              orderIndex
            }
            applications {
              id
              vacancyId
              currentStageId
              status
              createdAt
              candidate {
                id
                firstName
                lastName
                email
                phone
                source
                resumeUrl
              }
            }
          }
        }
      }
    `;
    return this.api.graphql<{ kanbanBoard: KanbanBoard }>(query, { vacancyId });
  }

  moveApplication(applicationId: string, stageId: string): Observable<{ moveApplication: Application }> {
    const mutation = `
      mutation MoveApplication($applicationId: UUID!, $stageId: UUID!) {
        moveApplication(applicationId: $applicationId, stageId: $stageId) {
          id
          vacancyId
          currentStageId
          status
        }
      }
    `;
    return this.api.graphql<{ moveApplication: Application }>(mutation, { applicationId, stageId });
  }

  applyToVacancy(vacancyId: string, candidate: CandidateInput): Observable<{ applyToVacancy: Application }> {
    const mutation = `
      mutation ApplyToVacancy($vacancyId: UUID!, $candidate: CandidateInput!) {
        applyToVacancy(vacancyId: $vacancyId, candidate: $candidate) {
          id
          vacancyId
          currentStageId
          status
          candidate {
            id
            firstName
            lastName
            email
            phone
            source
            resumeUrl
          }
        }
      }
    `;
    return this.api.graphql<{ applyToVacancy: Application }>(mutation, { vacancyId, candidate });
  }

  hireCandidate(applicationId: string, userId: string): Observable<{ hireCandidate: Application }> {
    const mutation = `
      mutation HireCandidate($applicationId: UUID!, $userId: UUID!) {
        hireCandidate(applicationId: $applicationId, userId: $userId) {
          id
          status
        }
      }
    `;
    return this.api.graphql<{ hireCandidate: Application }>(mutation, { applicationId, userId });
  }

  createVacancy(input: CreateVacancyInput): Observable<{ createVacancy: Vacancy }> {
    const mutation = `
      mutation CreateVacancy($input: CreateVacancyInput!) {
        createVacancy(input: $input) {
          id
          title
          description
          status
        }
      }
    `;
    return this.api.graphql<{ createVacancy: Vacancy }>(mutation, { input });
  }
}
