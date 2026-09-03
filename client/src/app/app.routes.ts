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
import { SettingsComponent } from './features/settings/settings.component';
import { authGuard, publicGuard } from './core/guards/auth.guard';

import { RecruitmentBoardComponent } from './features/recruitment/recruitment-board.component';
import { TaskBoardComponent } from './features/tasks/task-board.component';
import { CalendarComponent } from './features/calendar/calendar.component';
import { TalentComponent } from './features/talent/talent.component';
import { OnboardingPipelineComponent } from './features/talent/onboarding/onboarding-pipeline.component';
import { OffboardingPipelineComponent } from './features/talent/offboarding/offboarding-pipeline.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [publicGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [publicGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'calendar', component: CalendarComponent, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'settings', component: SettingsComponent, canActivate: [authGuard] },
  { path: 'members', component: MembersComponent, canActivate: [authGuard] },
  { path: 'members/:id', component: MemberProfileComponent, canActivate: [authGuard] },
  {
    path: 'talent',
    component: TalentComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'recruitment', pathMatch: 'full' },
      { path: 'recruitment', component: RecruitmentBoardComponent },
      { path: 'onboarding', component: OnboardingPipelineComponent },
      { path: 'offboarding', component: OffboardingPipelineComponent },
    ]
  },
  { path: 'recruitment', redirectTo: 'talent/recruitment', pathMatch: 'full' },
  { path: 'onboarding', redirectTo: 'talent/onboarding', pathMatch: 'full' },
  { path: 'offboarding', redirectTo: 'talent/offboarding', pathMatch: 'full' },
  { path: 'vacancies', redirectTo: 'talent/recruitment', pathMatch: 'full' },
  { path: 'candidates', redirectTo: 'talent/recruitment', pathMatch: 'full' },
  { path: 'tasks', component: TaskBoardComponent, canActivate: [authGuard] },
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

