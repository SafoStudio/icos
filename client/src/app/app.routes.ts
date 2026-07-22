import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { MembersComponent } from './features/members/members.component';
import { MemberProfileComponent } from './features/members/member-profile/member-profile.component';
import { OrganizationComponent } from './features/org/organization.component';
import { DepartmentsComponent } from './features/org/departments/departments.component';
import { DepartmentDetailComponent } from './features/org/departments/department-detail/department-detail.component';
import { PositionsComponent } from './features/org/positions/positions.component';
import { ProfileComponent } from './features/profile/profile.component';
import { authGuard, publicGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [publicGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [publicGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'members', component: MembersComponent, canActivate: [authGuard] },
  { path: 'members/:id', component: MemberProfileComponent, canActivate: [authGuard] },
  {
    path: 'organization',
    component: OrganizationComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'departments', pathMatch: 'full' },
      { path: 'departments', component: DepartmentsComponent },
      { path: 'departments/:id', component: DepartmentDetailComponent },
      { path: 'positions', component: PositionsComponent },
    ]
  },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: '/dashboard' }
];
