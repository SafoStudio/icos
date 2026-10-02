import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsService, MemberAnalytics, ObjectiveProgress } from '@services';
import { RadialGaugeComponent } from '../../../../shared/components/charts';

@Component({
  selector: 'app-member-dashboard',
  standalone: true,
  imports: [CommonModule, RadialGaugeComponent],
  templateUrl: './member-dashboard.component.html',
  styleUrls: ['./member-dashboard.component.scss']
})
export class MemberDashboardComponent implements OnInit {
  private readonly analyticsService = inject(AnalyticsService);

  readonly analytics = signal<MemberAnalytics | null>(null);
  readonly objectives = signal<ObjectiveProgress[]>([]);

  metrics = [
    { label: 'Active Tasks', value: '5', change: '2 due today', changeType: 'neutral' },
    { label: 'Completed Tasks', value: '24', change: '+8 this month', changeType: 'positive' },
    { label: 'Vacation Balance', value: '18 days', change: '6 used of 24', changeType: 'positive' },
    { label: 'OKR Alignment', value: '82%', change: 'Exceeding pace', changeType: 'positive' }
  ];

  tasks = [
    { title: 'Review Q3 Engineering Report', project: 'Platform Core', dueDate: 'Today', priority: 'High' },
    { title: 'Update API Documentation for GraphQL', project: 'API Gateway', dueDate: 'Tomorrow', priority: 'Medium' },
    { title: 'Sync with Team on Sprint Backlog', project: 'DevOps', dueDate: 'Sep 28', priority: 'Low' },
    { title: 'Conduct Code Review on Auth Module', project: 'Security', dueDate: 'Sep 30', priority: 'High' }
  ];

  ngOnInit(): void {
    this.analyticsService.getMemberAnalytics().subscribe({
      next: (data) => {
        this.analytics.set(data);
      }
    });

    this.analyticsService.getObjectiveProgress().subscribe({
      next: (objs) => {
        this.objectives.set(objs);
      }
    });
  }

  getGaugeColor(progress: number): string {
    if (progress >= 70) return '#10b981';
    if (progress >= 40) return '#f59e0b';
    return '#ef4444';
  }
}
