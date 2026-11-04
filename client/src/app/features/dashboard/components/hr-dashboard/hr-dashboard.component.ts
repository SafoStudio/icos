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
  imports: [CommonModule, FormsModule, BarChartComponent, DoughnutChartComponent, HeatmapComponent],
  templateUrl: './hr-dashboard.component.html',
  styleUrls: ['./hr-dashboard.component.scss']
})
export class HrDashboardComponent implements OnInit {
  private readonly analyticsService = inject(AnalyticsService);

  readonly analytics = signal<HrAnalytics | null>(null);
  readonly isLoading = signal<boolean>(true);

  readonly turnoverRate = signal(3.2);

  readonly heatmapData = signal<TimeOffDay[]>([]);
  readonly departments = signal<{ id: string; name: string }[]>([{ id: '', name: 'All Departments' }]);

  readonly funnelLabels = signal<string[]>([]);
  readonly funnelDatasets = signal<BarDataset[]>([]);
  readonly sourceLabels = signal<string[]>([]);
  readonly sourceData = signal<number[]>([]);
  readonly leaveLabels = signal<string[]>([]);
  readonly leaveData = signal<number[]>([]);
  readonly leaveColors = signal<string[]>([]);

  readonly metrics = signal([
    { label: 'Open Vacancies', value: '8', change: '2 critical', changeType: 'positive' },
    { label: 'Total Candidates', value: '142', change: '+28 this month', changeType: 'positive' },
    { label: 'Avg Time to Hire', value: '24 days', change: '-4 days vs Q1', changeType: 'positive' },
    { label: 'Turnover Rate', value: '3.2%', change: 'Healthy (benchmark <15%)', changeType: 'positive' }
  ]);

  candidates = [
    { name: 'Alice Johnson', position: 'Senior Frontend Developer', status: 'Interview', date: 'Today, 14:00' },
    { name: 'Bob Smith', position: 'Lead UX Designer', status: 'Review', date: 'Yesterday' },
    { name: 'Carol White', position: 'Technical Product Manager', status: 'Offer', date: '2 days ago' },
    { name: 'David Brown', position: 'DevOps / SRE Engineer', status: 'Applied', date: '3 days ago' }
  ];

  selectedHeatmapDept = '';
  selectedPeriodDays = 14;

  ngOnInit(): void {
    this.analyticsService.getHrAnalytics().subscribe({
      next: (data) => {
        this.analytics.set(data);
        this.setupCharts(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('HR analytics failed', err);
        this.isLoading.set(false);
      }
    });

    this.analyticsService.getExecutiveAnalytics().subscribe({
      next: (exec) => {
        this.turnoverRate.set(exec.turnoverRate);
        const list = this.metrics();
        const idx = list.findIndex(m => m.label === 'Turnover Rate');
        if (idx >= 0) {
          const updated = [...list];
          updated[idx] = {
            ...updated[idx],
            value: `${exec.turnoverRate}%`,
            change: exec.turnoverRate > 15 ? 'Above 15% critical alert' : 'Healthy pace (< 15%)',
            changeType: exec.turnoverRate > 15 ? 'negative' : 'positive'
          };
          this.metrics.set(updated);
        }
      },
      error: (err) => console.error('Executive analytics failed', err)
    });

    this.analyticsService.getHeadcountByDepartment().subscribe({
      next: (depts) => {
        if (depts && depts.length > 0) {
          this.departments.set([
            { id: '', name: 'All Departments' },
            ...depts.map(d => ({ id: d.departmentId, name: d.departmentName }))
          ]);
        }
      },
      error: (err) => console.error('Headcount failed', err)
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
      next: (days: TimeOffDay[]) => this.heatmapData.set(days),
      error: (err) => console.error('Heatmap failed', err)
    });
  }

  private setupCharts(data: HrAnalytics): void {
    if (!data) return;

    this.funnelLabels.set(
      data.funnelStages.map((s: FunnelStageMetric) => `${s.stage} (${s.conversionRate}%)`)
    );
    this.funnelDatasets.set([
      {
        label: 'Candidates Count',
        data: data.funnelStages.map((s: FunnelStageMetric) => s.count),
        backgroundColor: ['#6366f1', '#818cf8', '#a5b4fc', '#38bdf8', '#34d399', '#10b981'],
        borderRadius: 6
      }
    ]);

    this.sourceLabels.set(data.candidateSources.map((s: CandidateSourceMetric) => s.source));
    this.sourceData.set(data.candidateSources.map((s: CandidateSourceMetric) => s.count));

    this.leaveLabels.set(data.leaveUtilization.map((l: LeaveCategoryMetric) => l.type));
    this.leaveData.set(data.leaveUtilization.map((l: LeaveCategoryMetric) => l.daysUsed));
    this.leaveColors.set(data.leaveUtilization.map((l: LeaveCategoryMetric) => l.color));
  }
}
