import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  AnalyticsService,
  ExecutiveAnalytics,
  MonthlyMetric,
  DepartmentPayrollMetric,
  DepartmentHeadcountMetric,
  HeadcountPoint,
  SalaryBucket,
  BonusPoint
} from '@services';
import {
  BarChartComponent,
  BarDataset,
  LineChartComponent,
  LineDataset,
  DoughnutChartComponent,
  RadialGaugeComponent
} from '../../../../shared/components/charts';

@Component({
  selector: 'app-owner-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    BarChartComponent,
    LineChartComponent,
    DoughnutChartComponent,
    RadialGaugeComponent
  ],
  templateUrl: './owner-dashboard.component.html',
  styleUrls: ['./owner-dashboard.component.scss']
})
export class OwnerDashboardComponent implements OnInit {
  private readonly analyticsService = inject(AnalyticsService);

  readonly analytics = signal<ExecutiveAnalytics | null>(null);
  readonly isLoading = signal<boolean>(true);

  // Department filter
  selectedDepartmentId = '';
  departments: { id: string; name: string }[] = [
    { id: '', name: 'All Departments' }
  ];

  // Headcount & Turnover Trend
  headcountLabels: string[] = [];
  headcountDatasets: LineDataset[] = [];
  currentTurnoverRate = 3.2;

  // Department distribution
  deptLabels: string[] = [];
  deptData: number[] = [];

  // Payroll by Department & Grade distribution
  payrollLabels: string[] = [];
  payrollDatasets: BarDataset[] = [];

  salaryGradeLabels: string[] = [];
  salaryGradeDatasets: BarDataset[] = [];

  // Bonus & Base Salary Dynamics
  bonusLabels: string[] = [];
  bonusDatasets: LineDataset[] = [];

  metrics = [
    { label: 'Total Employees', value: '156', change: '+12 this quarter', changeType: 'positive' },
    { label: 'Monthly Payroll', value: '$284,500', change: '+4.2% vs budget', changeType: 'positive' },
    { label: 'Turnover Rate', value: '3.2%', change: '-0.8% YoY (healthy)', changeType: 'positive' },
    { label: 'Company OKR', value: '78%', change: 'On track for Q3', changeType: 'positive' }
  ];

  members = [
    { name: 'John Doe', position: 'CEO', department: 'Executive', status: 'Active', email: 'john.doe@acme.com' },
    { name: 'Jane Smith', position: 'HR Director', department: 'Human Resources', status: 'Active', email: 'jane.smith@acme.com' },
    { name: 'Mike Johnson', position: 'Lead Developer', department: 'Engineering', status: 'Active', email: 'mike.johnson@acme.com' },
    { name: 'Sarah Williams', position: 'Product Manager', department: 'Product', status: 'On Leave', email: 'sarah.williams@acme.com' },
    { name: 'Alex Brown', position: 'Marketing Lead', department: 'Marketing', status: 'Active', email: 'alex.brown@acme.com' }
  ];

  projects = [
    { name: 'Website Redesign', team: '6', deadline: '2026-08-15', status: 'In Progress', priority: 'High' },
    { name: 'Mobile App Development', team: '8', deadline: '2026-09-01', status: 'In Progress', priority: 'High' },
    { name: 'Marketing Campaign', team: '4', deadline: '2026-07-30', status: 'Review', priority: 'Medium' },
    { name: 'API Integration', team: '3', deadline: '2026-08-10', status: 'Completed', priority: 'Low' },
    { name: 'Security Audit', team: '5', deadline: '2026-08-25', status: 'Planned', priority: 'Medium' }
  ];

  ngOnInit(): void {
    this.loadExecutiveData();
    this.loadHeadcountTrend();
    this.loadSalaryAnalytics();
    this.loadBonusDynamics();
  }

  private loadExecutiveData(): void {
    this.analyticsService.getExecutiveAnalytics().subscribe({
      next: (data) => {
        this.analytics.set(data);
        this.currentTurnoverRate = data.turnoverRate;
        this.setupOverviewCharts(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });

    this.analyticsService.getHeadcountByDepartment().subscribe({
      next: (depts) => {
        if (depts && depts.length > 0) {
          this.departments = [
            { id: '', name: 'All Departments' },
            ...depts.map(d => ({ id: d.departmentId, name: d.departmentName }))
          ];
        }
      }
    });
  }

  onDepartmentChange(deptId: string): void {
    this.selectedDepartmentId = deptId;
    this.loadHeadcountTrend(deptId || undefined);
    this.loadSalaryAnalytics(deptId || undefined);
  }

  private loadHeadcountTrend(deptId?: string): void {
    this.analyticsService.getHeadcountTrend(12, deptId).subscribe({
      next: (points: HeadcountPoint[]) => {
        this.headcountLabels = points.map(p => p.period);
        this.headcountDatasets = [
          {
            label: 'Headcount',
            data: points.map(p => p.headcount),
            borderColor: '#6366f1',
            backgroundColor: 'rgba(99, 102, 241, 0.15)',
            tension: 0.35,
            fill: true
          },
          {
            label: 'Hired',
            data: points.map(p => p.hired),
            borderColor: '#10b981',
            backgroundColor: 'transparent',
            tension: 0.2,
            fill: false
          },
          {
            label: 'Fired / Left',
            data: points.map(p => p.fired),
            borderColor: '#ef4444',
            backgroundColor: 'transparent',
            tension: 0.2,
            fill: false
          }
        ];

        if (points.length > 0) {
          const lastPoint = points[points.length - 1];
          this.currentTurnoverRate = lastPoint.turnoverRate;
        }
      }
    });
  }

  private loadSalaryAnalytics(deptId?: string): void {
    this.analyticsService.getSalaryDistribution(deptId).subscribe({
      next: (buckets: SalaryBucket[]) => {
        this.salaryGradeLabels = buckets.map(b => b.label);
        this.salaryGradeDatasets = [
          {
            label: 'Average Salary ($)',
            data: buckets.map(b => b.avg),
            backgroundColor: '#38bdf8',
            borderRadius: 6
          },
          {
            label: 'Max Salary ($)',
            data: buckets.map(b => b.max),
            backgroundColor: 'rgba(99, 102, 241, 0.65)',
            borderRadius: 6
          }
        ];
      }
    });
  }

  private loadBonusDynamics(): void {
    this.analyticsService.getBonusDynamics(12).subscribe({
      next: (points: BonusPoint[]) => {
        this.bonusLabels = points.map(p => p.period);
        this.bonusDatasets = [
          {
            label: 'Base Payroll ($)',
            data: points.map(p => p.totalBase),
            borderColor: '#6366f1',
            backgroundColor: 'transparent',
            tension: 0.3
          },
          {
            label: 'Bonus Pool ($)',
            data: points.map(p => p.totalBonus),
            borderColor: '#f59e0b',
            backgroundColor: 'rgba(245, 158, 11, 0.12)',
            tension: 0.3,
            fill: true
          }
        ];
      }
    });
  }

  private setupOverviewCharts(data: ExecutiveAnalytics): void {
    // Payroll by Department
    this.payrollLabels = data.payrollByDepartment.map((p: DepartmentPayrollMetric) => p.department);
    this.payrollDatasets = [
      {
        label: 'Total Budget ($)',
        data: data.payrollByDepartment.map((p: DepartmentPayrollMetric) => p.totalPayroll),
        backgroundColor: '#6366f1',
        borderRadius: 6
      }
    ];

    // Department distribution
    this.deptLabels = data.departmentDistribution.map((d: DepartmentHeadcountMetric) => d.department);
    this.deptData = data.departmentDistribution.map((d: DepartmentHeadcountMetric) => d.headcount);
  }
}
