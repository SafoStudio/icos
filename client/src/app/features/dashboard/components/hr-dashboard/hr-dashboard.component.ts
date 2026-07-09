import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-hr-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './hr-dashboard.component.html',
  styleUrls: ['./hr-dashboard.component.scss']
})
export class HrDashboardComponent {
  metrics = [
    { label: 'Open Positions', value: '8', change: '+2', changeType: 'positive' },
    { label: 'Total Candidates', value: '47', change: '+15', changeType: 'positive' },
    { label: 'Interviews Today', value: '6', change: '2 scheduled', changeType: 'neutral' },
    { label: 'Pending Reviews', value: '12', change: '5 urgent', changeType: 'negative' }
  ];

  candidates = [
    { name: 'Alice Johnson', position: 'Frontend Developer', status: 'Interview', date: 'Today' },
    { name: 'Bob Smith', position: 'UX Designer', status: 'Review', date: 'Yesterday' },
    { name: 'Carol White', position: 'Project Manager', status: 'Offer', date: '2 days ago' },
    { name: 'David Brown', position: 'Backend Developer', status: 'Applied', date: '3 days ago' }
  ];
}
