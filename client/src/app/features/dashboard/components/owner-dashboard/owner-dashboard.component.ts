import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AnalyticsService,
  ExecutiveAnalytics,
  MonthlyMetric,
  DepartmentPayrollMetric,
  DepartmentHeadcountMetric
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

  // Chart datasets
  headcountLabels: string[] = [];
  headcountDatasets: LineDataset[] = [];

  payrollLabels: string[] = [];
  payrollDatasets: BarDataset[] = [];

  deptLabels: string[] = [];
  deptData: number[] = [];

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
    this.analyticsService.getExecutiveAnalytics().subscribe({
      next: (data) => {
        this.analytics.set(data);
        this.setupCharts(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  private setupCharts(data: ExecutiveAnalytics): void {
    // 1. Headcount & Turnover Trend
    this.headcountLabels = data.headcountTrends.map((t: MonthlyMetric) => t.month);
    this.headcountDatasets = [
      {
        label: 'Total Headcount',
        data: data.headcountTrends.map((t: MonthlyMetric) => t.headcount),
        borderColor: '#6366f1',
        backgroundColor: 'rgba(99, 102, 241, 0.15)',
        tension: 0.35,
        fill: true
      },
      {
        label: 'New Hires',
        data: data.headcountTrends.map((t: MonthlyMetric) => t.newHires),
        borderColor: '#10b981',
        backgroundColor: 'transparent',
        tension: 0.2,
        fill: false
      }
    ];

    // 2. Payroll by Department
    this.payrollLabels = data.payrollByDepartment.map((p: DepartmentPayrollMetric) => p.department);
    this.payrollDatasets = [
      {
        label: 'Total Budget ($)',
        data: data.payrollByDepartment.map((p: DepartmentPayrollMetric) => p.totalPayroll),
        backgroundColor: '#6366f1',
        borderRadius: 6
      }
    ];

    // 3. Department distribution
    this.deptLabels = data.departmentDistribution.map((d: DepartmentHeadcountMetric) => d.department);
    this.deptData = data.departmentDistribution.map((d: DepartmentHeadcountMetric) => d.headcount);
  }
}
