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

export interface HeadcountPoint {
  period: string;
  headcount: number;
  hired: number;
  fired: number;
  turnoverRate: number;
}

export interface DepartmentHeadcount {
  departmentId: string;
  departmentName: string;
  points: HeadcountPoint[];
}

export interface TimeOffDay {
  date: string;
  departmentId: string;
  departmentName: string;
  absentCount: number;
  totalCount: number;
  loadPercent: number;
}

export interface SalaryBucket {
  label: string;
  departmentId?: string;
  departmentName?: string;
  min: number;
  max: number;
  avg: number;
  count: number;
}

export interface BonusPoint {
  period: string;
  totalBonus: number;
  totalBase: number;
}

export interface KeyResultProgress {
  id: string;
  title: string;
  progress: number;
  weight: number;
  status: string;
}

export interface ObjectiveProgress {
  id: string;
  title: string;
  progress: number;
  keyResults: KeyResultProgress[];
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

  getHeadcountTrend(months: number = 12, departmentId?: string): Observable<HeadcountPoint[]> {
    const fallbackPoints: HeadcountPoint[] = [
      { period: '2026-01', headcount: 122, hired: 6, fired: 1, turnoverRate: 0.8 },
      { period: '2026-02', headcount: 128, hired: 8, fired: 2, turnoverRate: 1.6 },
      { period: '2026-03', headcount: 133, hired: 7, fired: 2, turnoverRate: 1.5 },
      { period: '2026-04', headcount: 137, hired: 5, fired: 1, turnoverRate: 0.7 },
      { period: '2026-05', headcount: 142, hired: 7, fired: 2, turnoverRate: 1.4 },
      { period: '2026-06', headcount: 145, hired: 5, fired: 2, turnoverRate: 1.4 },
      { period: '2026-07', headcount: 148, hired: 4, fired: 1, turnoverRate: 0.7 },
      { period: '2026-08', headcount: 150, hired: 3, fired: 1, turnoverRate: 0.7 },
      { period: '2026-09', headcount: 156, hired: 8, fired: 2, turnoverRate: 1.3 }
    ];

    const query = `
      query GetHeadcountTrend($months: Int, $departmentId: ID) {
        headcountTrend(months: $months, departmentId: $departmentId) {
          period
          headcount
          hired
          fired
          turnoverRate
        }
      }
    `;

    return this.api.graphql<{ headcountTrend: HeadcountPoint[] }>(query, { months, departmentId }).pipe(
      map(res => res.headcountTrend || fallbackPoints),
      catchError(() => of(fallbackPoints))
    );
  }

  getHeadcountByDepartment(months: number = 12): Observable<DepartmentHeadcount[]> {
    const query = `
      query GetHeadcountByDepartment($months: Int) {
        headcountByDepartment(months: $months) {
          departmentId
          departmentName
          points {
            period
            headcount
            hired
            fired
            turnoverRate
          }
        }
      }
    `;

    return this.api.graphql<{ headcountByDepartment: DepartmentHeadcount[] }>(query, { months }).pipe(
      map(res => res.headcountByDepartment || []),
      catchError(() => of([]))
    );
  }

  getTimeOffHeatmap(from?: string, to?: string, departmentId?: string): Observable<TimeOffDay[]> {
    const query = `
      query GetTimeOffHeatmap($from: String!, $to: String!, $departmentId: ID) {
        timeOffHeatmap(from: $from, to: $to, departmentId: $departmentId) {
          date
          departmentId
          departmentName
          absentCount
          totalCount
          loadPercent
        }
      }
    `;

    const now = new Date();
    const defaultFrom = from || new Date(now.getTime() - 14 * 86400000).toISOString().split('T')[0];
    const defaultTo = to || new Date(now.getTime() + 14 * 86400000).toISOString().split('T')[0];

    return this.api.graphql<{ timeOffHeatmap: TimeOffDay[] }>(query, {
      from: defaultFrom,
      to: defaultTo,
      departmentId
    }).pipe(
      map(res => res.timeOffHeatmap || []),
      catchError(() => {
        // Fallback heatmap items
        const depts = ['Engineering', 'Product & Design', 'Sales & Marketing', 'Operations'];
        const fallback: TimeOffDay[] = [];
        const start = new Date(defaultFrom);
        const end = new Date(defaultTo);
        for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
          const dateStr = d.toISOString().split('T')[0];
          depts.forEach((dept, idx) => {
            const absent = (d.getDate() + idx) % 5 === 0 ? 3 : ((d.getDate() + idx) % 3 === 0 ? 1 : 0);
            const total = 25;
            fallback.push({
              date: dateStr,
              departmentId: `dept-${idx}`,
              departmentName: dept,
              absentCount: absent,
              totalCount: total,
              loadPercent: Math.round((absent / total) * 1000) / 10
            });
          });
        }
        return of(fallback);
      })
    );
  }

  getSalaryDistribution(departmentId?: string): Observable<SalaryBucket[]> {
    const fallbackBuckets: SalaryBucket[] = [
      { label: 'Junior (< $2.5k)', min: 1500, max: 2500, avg: 2100, count: 28 },
      { label: 'Mid-Level ($2.5k - $4.5k)', min: 2600, max: 4500, avg: 3400, count: 64 },
      { label: 'Senior ($4.5k - $7k)', min: 4600, max: 7000, avg: 5600, count: 42 },
      { label: 'Lead & Exec ($7k+)', min: 7200, max: 12500, avg: 8900, count: 22 }
    ];

    const query = `
      query GetSalaryDistribution($departmentId: ID) {
        salaryDistribution(departmentId: $departmentId) {
          label
          departmentId
          departmentName
          min
          max
          avg
          count
        }
      }
    `;

    return this.api.graphql<{ salaryDistribution: SalaryBucket[] }>(query, { departmentId }).pipe(
      map(res => res.salaryDistribution || fallbackBuckets),
      catchError(() => of(fallbackBuckets))
    );
  }

  getBonusDynamics(months: number = 12): Observable<BonusPoint[]> {
    const query = `
      query GetBonusDynamics($months: Int) {
        bonusDynamics(months: $months) {
          period
          totalBonus
          totalBase
        }
      }
    `;

    const fallbackPoints: BonusPoint[] = [
      { period: '2026-01', totalBase: 250000, totalBonus: 12500 },
      { period: '2026-02', totalBase: 253000, totalBonus: 13000 },
      { period: '2026-03', totalBase: 257000, totalBonus: 42000 },
      { period: '2026-04', totalBase: 260000, totalBonus: 12000 },
      { period: '2026-05', totalBase: 264000, totalBonus: 14500 },
      { period: '2026-06', totalBase: 268000, totalBonus: 45000 },
      { period: '2026-07', totalBase: 272000, totalBonus: 13500 },
      { period: '2026-08', totalBase: 275000, totalBonus: 14000 },
      { period: '2026-09', totalBase: 284500, totalBonus: 48000 }
    ];

    return this.api.graphql<{ bonusDynamics: BonusPoint[] }>(query, { months }).pipe(
      map(res => res.bonusDynamics || fallbackPoints),
      catchError(() => of(fallbackPoints))
    );
  }

  getObjectiveProgress(teamId?: string, period?: string): Observable<ObjectiveProgress[]> {
    const fallbackOkr: ObjectiveProgress[] = [
      {
        id: '11111111-1111-1111-1111-111111111111',
        title: 'Scale Infrastructure to 99.99% Availability',
        progress: 85,
        keyResults: [
          { id: '1', title: 'Reduce MTTR incident duration below 15 mins', progress: 90, weight: 0.4, status: 'ON_TRACK' },
          { id: '2', title: 'Migrate Kubernetes clusters to multi-region', progress: 80, weight: 0.6, status: 'ON_TRACK' }
        ]
      },
      {
        id: '22222222-2222-2222-2222-222222222222',
        title: 'Accelerate Enterprise Customer Pipeline',
        progress: 72.5,
        keyResults: [
          { id: '3', title: 'Close $1.2M ARR in Fortune 500 deals', progress: 75, weight: 0.5, status: 'ON_TRACK' },
          { id: '4', title: 'Launch partner co-selling program with 5 ISVs', progress: 70, weight: 0.5, status: 'ON_TRACK' }
        ]
      },
      {
        id: '33333333-3333-3333-3333-333333333333',
        title: 'Improve Developer Velocity & Core Quality',
        progress: 45,
        keyResults: [
          { id: '5', title: 'Increase unit & integration test coverage to 85%', progress: 50, weight: 0.5, status: 'AT_RISK' },
          { id: '6', title: 'Cut CI/CD pipeline build times from 18m to 6m', progress: 40, weight: 0.5, status: 'AT_RISK' }
        ]
      }
    ];

    const query = `
      query GetObjectiveProgress($teamId: ID, $period: String) {
        objectiveProgress(teamId: $teamId, period: $period) {
          id
          title
          progress
          keyResults {
            id
            title
            progress
            weight
            status
          }
        }
      }
    `;

    return this.api.graphql<{ objectiveProgress: ObjectiveProgress[] }>(query, { teamId, period }).pipe(
      map(res => res.objectiveProgress || fallbackOkr),
      catchError(() => of(fallbackOkr))
    );
  }
}
