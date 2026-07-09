import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-member-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './member-dashboard.component.html',
  styleUrls: ['./member-dashboard.component.scss']
})
export class MemberDashboardComponent {
  metrics = [
    { label: 'My Tasks', value: '8', change: '3 overdue', changeType: 'negative' },
    { label: 'Projects', value: '3', change: '1 active', changeType: 'neutral' },
    { label: 'Meetings Today', value: '2', change: '1 in 2 hours', changeType: 'neutral' },
    { label: 'Notifications', value: '5', change: '2 unread', changeType: 'negative' }
  ];

  tasks = [
    { title: 'Review Q4 Report', project: 'Financial Analysis', dueDate: 'Today', priority: 'High' },
    { title: 'Update Documentation', project: 'API Development', dueDate: 'Tomorrow', priority: 'Medium' },
    { title: 'Prepare Presentation', project: 'Marketing', dueDate: 'Feb 15', priority: 'Low' },
    { title: 'Code Review', project: 'Frontend', dueDate: 'Feb 18', priority: 'High' }
  ];
}
