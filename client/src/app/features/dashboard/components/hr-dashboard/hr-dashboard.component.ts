import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  AnalyticsService,
  HrAnalytics,
  FunnelStageMetric,
  CandidateSourceMetric,
  LeaveCategoryMetric,
  TimeOffDay
} from '@services';
import {
  BarChartComponent,
  BarDataset,
  DoughnutChartComponent,
  HeatmapComponent
} from '../../../../shared/components/charts';

@Component({
  selector: 'app-hr-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    BarChartComponent,
    DoughnutChartComponent,
    HeatmapComponent
  ],
  templateUrl: './hr-dashboard.component.html',
  styleUrls: ['./hr-dashboard.component.scss']
})
export class HrDashboardComponent implements OnInit {
  private readonly analyticsService = inject(AnalyticsService);

  readonly analytics = signal<HrAnalytics | null>(null);
  readonly isLoading = signal<boolean>(true);

  // Turnover rate tracking
  turnoverRate = 3.2;

  // Time-Off Heatmap
  heatmapData: TimeOffDay[] = [];
  selectedHeatmapDept = '';
  selectedPeriodDays = 14;

  departments: { id: string; name: string }[] = [
    { id: '', name: 'All Departments' }
  ];

  // Hiring Funnel datasets
  funnelLabels: string[] = [];
  funnelDatasets: BarDataset[] = [];

  // Sources datasets
  sourceLabels: string[] = [];
  sourceData: number[] = [];

  // Leave breakdown datasets
  leaveLabels: string[] = [];
  leaveData: number[] = [];
  leaveColors: string[] = [];

  metrics = [
    { label: 'Open Vacancies', value: '8', change: '2 critical', changeType: 'positive' },
    { label: 'Total Candidates', value: '142', change: '+28 this month', changeType: 'positive' },
    { label: 'Avg Time to Hire', value: '24 days', change: '-4 days vs Q1', changeType: 'positive' },
    { label: 'Turnover Rate', value: '3.2%', change: 'Healthy (benchmark <15%)', changeType: 'positive' }
  ];

  candidates = [
    { name: 'Alice Johnson', position: 'Senior Frontend Developer', status: 'Interview', date: 'Today, 14:00' },
    { name: 'Bob Smith', position: 'Lead UX Designer', status: 'Review', date: 'Yesterday' },
    { name: 'Carol White', position: 'Technical Product Manager', status: 'Offer', date: '2 days ago' },
    { name: 'David Brown', position: 'DevOps / SRE Engineer', status: 'Applied', date: '3 days ago' }
  ];

  ngOnInit(): void {
    this.analyticsService.getHrAnalytics().subscribe({
      next: (data) => {
        this.analytics.set(data);
        this.setupCharts(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });

    this.analyticsService.getExecutiveAnalytics().subscribe({
      next: (exec) => {
        this.turnoverRate = exec.turnoverRate;
        const turnoverMetric = this.metrics.find(m => m.label === 'Turnover Rate');
        if (turnoverMetric) {
          turnoverMetric.value = `${exec.turnoverRate}%`;
          turnoverMetric.change = exec.turnoverRate > 15 ? 'Above 15% critical alert' : 'Healthy pace (< 15%)';
          turnoverMetric.changeType = exec.turnoverRate > 15 ? 'negative' : 'positive';
        }
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

    this.loadHeatmap();
  }

  onFilterChange(): void {
    this.loadHeatmap();
  }

  private loadHeatmap(): void {
    const now = new Date();
    const fromDate = new Date(now.getTime() - 7 * 86400000).toISOString().split('T')[0];
    const toDate = new Date(now.getTime() + (this.selectedPeriodDays - 7) * 86400000).toISOString().split('T')[0];

    this.analyticsService.getTimeOffHeatmap(fromDate, toDate, this.selectedHeatmapDept || undefined).subscribe({
      next: (days: TimeOffDay[]) => {
        this.heatmapData = days;
      }
    });
  }

  private setupCharts(data: HrAnalytics): void {
    // 1. Hiring Funnel (Horizontal Bar)
    this.funnelLabels = data.funnelStages.map((s: FunnelStageMetric) => `${s.stage} (${s.conversionRate}%)`);
    this.funnelDatasets = [
      {
        label: 'Candidates Count',
        data: data.funnelStages.map((s: FunnelStageMetric) => s.count),
        backgroundColor: [
          '#6366f1',
          '#818cf8',
          '#a5b4fc',
          '#38bdf8',
          '#34d399',
          '#10b981'
        ],
        borderRadius: 6
      }
    ];

    // 2. Candidate Acquisition Sources (Doughnut)
    this.sourceLabels = data.candidateSources.map((s: CandidateSourceMetric) => s.source);
    this.sourceData = data.candidateSources.map((s: CandidateSourceMetric) => s.count);

    // 3. Leave Utilization by Category (Doughnut)
    this.leaveLabels = data.leaveUtilization.map((l: LeaveCategoryMetric) => l.type);
    this.leaveData = data.leaveUtilization.map((l: LeaveCategoryMetric) => l.daysUsed);
    this.leaveColors = data.leaveUtilization.map((l: LeaveCategoryMetric) => l.color);
  }
}
