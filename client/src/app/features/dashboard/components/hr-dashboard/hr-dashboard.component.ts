import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AnalyticsService,
  HrAnalytics,
  FunnelStageMetric,
  CandidateSourceMetric,
  LeaveCategoryMetric
} from '@services';
import {
  BarChartComponent,
  BarDataset,
  DoughnutChartComponent
} from '../../../../shared/components/charts';

@Component({
  selector: 'app-hr-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    BarChartComponent,
    DoughnutChartComponent
  ],
  templateUrl: './hr-dashboard.component.html',
  styleUrls: ['./hr-dashboard.component.scss']
})
export class HrDashboardComponent implements OnInit {
  private readonly analyticsService = inject(AnalyticsService);

  readonly analytics = signal<HrAnalytics | null>(null);
  readonly isLoading = signal<boolean>(true);

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
    { label: 'Offer Acceptance', value: '88%', change: '+6% benchmark', changeType: 'positive' }
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
