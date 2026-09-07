import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-owner-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './owner-dashboard.component.html',
  styleUrls: ['./owner-dashboard.component.scss']
})
export class OwnerDashboardComponent {
  metrics = [
    { label: 'Total Employees', value: '156', change: '+12', changeType: 'positive' },
    { label: 'Active Projects', value: '8', change: '+2', changeType: 'positive' },
    { label: 'Pending Tasks', value: '23', change: '5 overdue', changeType: 'negative' },
    { label: 'Revenue This Month', value: '$284,500', change: '+8.3%', changeType: 'positive' }
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
}
