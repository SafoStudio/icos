import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
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
  readonly authService = inject(AuthService);

  userRole = computed(() => {
    const user = this.authService.currentUser();
    return user?.role || 'SPECIALIST';
  });
}
