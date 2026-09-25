import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ApiService } from './api.service';

export interface MonthlyMetric {
  month: string;
  headcount: number;
  newHires: number;
  turnover: number;
}

export interface DepartmentHeadcountMetric {
  department: string;
  headcount: number;
  percentage: number;
}

export interface DepartmentPayrollMetric {
  department: string;
  totalPayroll: number;
  averageSalary: number;
}

export interface ExecutiveAnalytics {
  totalEmployees: number;
  activeProjects: number;
  turnoverRate: number;
  monthlyPayroll: number;
  overallOkrProgress: number;
  headcountTrends: MonthlyMetric[];
  departmentDistribution: DepartmentHeadcountMetric[];
  payrollByDepartment: DepartmentPayrollMetric[];
}

export interface FunnelStageMetric {
  stage: string;
  count: number;
  conversionRate: number;
}

export interface CandidateSourceMetric {
  source: string;
  count: number;
}

export interface LeaveCategoryMetric {
  type: string;
  daysUsed: number;
  color: string;
}

export interface HrAnalytics {
  openVacancies: number;
  totalCandidates: number;
  avgTimeToHireDays: number;
  offerAcceptanceRate: number;
  funnelStages: FunnelStageMetric[];
  candidateSources: CandidateSourceMetric[];
  leaveUtilization: LeaveCategoryMetric[];
}

export interface MemberAnalytics {
  personalOkrProgress: number;
  tasksCompleted: number;
  tasksPending: number;
  leaveDaysRemaining: number;
  leaveDaysUsed: number;
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private readonly api = inject(ApiService);

  getExecutiveAnalytics(): Observable<ExecutiveAnalytics> {
    const fallbackData: ExecutiveAnalytics = {
      totalEmployees: 156,
      activeProjects: 8,
      turnoverRate: 3.2,
      monthlyPayroll: 284500,
      overallOkrProgress: 78,
      headcountTrends: [
        { month: 'Jan', headcount: 122, newHires: 6, turnover: 1 },
        { month: 'Feb', headcount: 128, newHires: 8, turnover: 2 },
        { month: 'Mar', headcount: 133, newHires: 7, turnover: 2 },
        { month: 'Apr', headcount: 137, newHires: 5, turnover: 1 },
        { month: 'May', headcount: 142, newHires: 7, turnover: 2 },
        { month: 'Jun', headcount: 145, newHires: 5, turnover: 2 },
        { month: 'Jul', headcount: 148, newHires: 4, turnover: 1 },
        { month: 'Aug', headcount: 150, newHires: 3, turnover: 1 },
        { month: 'Sep', headcount: 156, newHires: 8, turnover: 2 }
      ],
      departmentDistribution: [
        { department: 'Engineering', headcount: 62, percentage: 40 },
        { department: 'Product & Design', headcount: 28, percentage: 18 },
        { department: 'Sales & Marketing', headcount: 34, percentage: 22 },
        { department: 'Human Resources', headcount: 16, percentage: 10 },
        { department: 'Finance & Legal', headcount: 16, percentage: 10 }
      ],
      payrollByDepartment: [
        { department: 'Engineering', totalPayroll: 125000, averageSalary: 2016 },
        { department: 'Product & Design', totalPayroll: 54000, averageSalary: 1928 },
        { department: 'Sales & Marketing', totalPayroll: 58000, averageSalary: 1705 },
        { department: 'Human Resources', totalPayroll: 24500, averageSalary: 1531 },
        { department: 'Finance & Legal', totalPayroll: 23000, averageSalary: 1437 }
      ]
    };

    const query = `
      query GetExecutiveAnalytics {
        executiveAnalytics {
          totalEmployees
          activeProjects
          turnoverRate
          monthlyPayroll
          overallOkrProgress
        }
      }
    `;

    return this.api.graphql<{ executiveAnalytics: Partial<ExecutiveAnalytics> }>(query).pipe(
      map(res => ({
        ...fallbackData,
        ...res.executiveAnalytics
      })),
      catchError(() => of(fallbackData))
    );
  }

  getHrAnalytics(): Observable<HrAnalytics> {
    const fallbackData: HrAnalytics = {
      openVacancies: 8,
      totalCandidates: 142,
      avgTimeToHireDays: 24,
      offerAcceptanceRate: 88,
      funnelStages: [
        { stage: 'Applied', count: 142, conversionRate: 100 },
        { stage: 'Screening', count: 96, conversionRate: 67.6 },
        { stage: 'Tech Interview', count: 48, conversionRate: 33.8 },
        { stage: 'Cultural Fit', count: 24, conversionRate: 16.9 },
        { stage: 'Offer Sent', count: 16, conversionRate: 11.2 },
        { stage: 'Hired', count: 14, conversionRate: 9.8 }
      ],
      candidateSources: [
        { source: 'LinkedIn Recruiter', count: 64 },
        { source: 'Employee Referrals', count: 38 },
        { source: 'Career Site', count: 24 },
        { source: 'Agency Partners', count: 16 }
      ],
      leaveUtilization: [
        { type: 'Annual Leave', daysUsed: 148, color: '#10b981' },
        { type: 'Remote Days', daysUsed: 92, color: '#3b82f6' },
        { type: 'Sick Leave', daysUsed: 36, color: '#f59e0b' },
        { type: 'Unpaid Leave', daysUsed: 12, color: '#94a3b8' }
      ]
    };

    const query = `
      query GetRecruitmentAnalytics {
        recruitmentAnalytics {
          openVacancies
          totalCandidates
          avgTimeToHireDays
          offerAcceptanceRate
        }
      }
    `;

    return this.api.graphql<{ recruitmentAnalytics: Partial<HrAnalytics> }>(query).pipe(
      map(res => ({
        ...fallbackData,
        ...res.recruitmentAnalytics
      })),
      catchError(() => of(fallbackData))
    );
  }

  getMemberAnalytics(): Observable<MemberAnalytics> {
    const fallbackData: MemberAnalytics = {
      personalOkrProgress: 82,
      tasksCompleted: 24,
      tasksPending: 5,
      leaveDaysRemaining: 18,
      leaveDaysUsed: 6
    };

    return of(fallbackData);
  }
}
