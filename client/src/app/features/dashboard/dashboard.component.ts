import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PermissionService } from '@services';
import { OwnerDashboardComponent } from './components/owner-dashboard/owner-dashboard.component';
import { HrDashboardComponent } from './components/hr-dashboard/hr-dashboard.component';
import { MemberDashboardComponent } from './components/member-dashboard/member-dashboard.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    OwnerDashboardComponent,
    HrDashboardComponent,
    MemberDashboardComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
  private readonly permissionService = inject(PermissionService);

  readonly dashboardType = computed<'OWNER' | 'HR' | 'MEMBER'>(() => {
    if (this.permissionService.isAdmin()) {
      return 'OWNER';
    }
    if (this.permissionService.isHr()) {
      return 'HR';
    }
    return 'MEMBER';
  });
}
