import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { DepartmentsService } from '../departments.service';
import { AuthService, ToastService, PermissionService } from '@services';
import { type Department, type Permission, type DepartmentMember, type UserRole, ROLES } from '@models';
import { NgIconComponent } from '@ng-icons/core';
import { DepartmentPermissionsComponent } from './components/department-permissions/department-permissions.component';
import { DepartmentMembersComponent } from './components/department-members/department-members.component';

@Component({
  selector: 'app-department-detail',
  standalone: true,
  imports: [CommonModule, DepartmentPermissionsComponent, DepartmentMembersComponent, NgIconComponent],
  templateUrl: './department-detail.component.html',
  styleUrls: ['./department-detail.component.scss']
})
export class DepartmentDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly departmentsService = inject(DepartmentsService);
  private readonly authService = inject(AuthService);
  private readonly permissionService = inject(PermissionService);
  private readonly toastService = inject(ToastService);

  departmentId = signal<string | null>(null);
  department = signal<Department | null>(null);
  loading = signal(false);
  members = signal<DepartmentMember[]>([]);
  allPermissions = signal<Permission[]>([]);
  headPermissions = signal<Permission[]>([]);
  specialistPermissions = signal<Permission[]>([]);
  saving = signal(false);

  readonly canEdit = computed(() => {
    return this.permissionService.isAdmin() || this.permissionService.hasPermission('department:manage');
  });

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.departmentId.set(id);
      this.loadData(id);
    }
  }

  private async loadData(departmentId: string) {
    this.loading.set(true);
    try {
      await Promise.all([
        this.loadDepartment(departmentId),
        this.loadMembers(departmentId),
        this.loadAllPermissions(),
        this.loadDepartmentPermissions(departmentId)
      ]);
    } catch (error) {
      console.error('Error loading department data', error);
      this.toastService.error('Failed to load department details.');
    } finally {
      this.loading.set(false);
    }
  }

  private loadDepartment(id: string): Promise<void> {
    return new Promise((resolve) => {
      this.departmentsService.getDepartment(id).subscribe({
        next: (dept) => { this.department.set(dept); resolve(); },
        error: () => resolve()
      });
    });
  }

  private loadMembers(departmentId: string): Promise<void> {
    return new Promise((resolve) => {
      this.departmentsService.getDepartmentMembers(departmentId).subscribe({
        next: (users) => { this.members.set(users); resolve(); },
        error: () => resolve()
      });
    });
  }

  private loadAllPermissions(): Promise<void> {
    return new Promise((resolve) => {
      this.departmentsService.getAllPermissions().subscribe({
        next: (perms) => { this.allPermissions.set(perms); resolve(); },
        error: () => resolve()
      });
    });
  }

  private loadDepartmentPermissions(departmentId: string): Promise<void> {
    return Promise.all([
      new Promise<void>((resolve) => {
        this.departmentsService.getDepartmentPermissions(departmentId, ROLES.HEAD).subscribe({
          next: (perms) => { this.headPermissions.set(perms); resolve(); },
          error: () => resolve()
        });
      }),
      new Promise<void>((resolve) => {
        this.departmentsService.getDepartmentPermissions(departmentId, ROLES.SPECIALIST).subscribe({
          next: (perms) => { this.specialistPermissions.set(perms); resolve(); },
          error: () => resolve()
        });
      })
    ]).then(() => {});
  }

  onTogglePermission(event: { permission: Permission; role: UserRole }): void {
    const { permission, role } = event;
    if (!this.canEdit()) return;

    const isAssigned = role === ROLES.HEAD
      ? this.headPermissions().some(p => p.id === permission.id)
      : this.specialistPermissions().some(p => p.id === permission.id);

    const deptId = this.departmentId()!;
    this.saving.set(true);

    const action = isAssigned
      ? this.departmentsService.revokeDepartmentPermission(deptId, role, permission.id)
      : this.departmentsService.assignDepartmentPermission(deptId, role, permission.id);

    action.subscribe({
      next: () => {
        if (isAssigned) {
          if (role === ROLES.HEAD) {
            this.headPermissions.update(list => list.filter(p => p.id !== permission.id));
          } else {
            this.specialistPermissions.update(list => list.filter(p => p.id !== permission.id));
          }
          this.toastService.success(`Permission revoked from ${role}`);
        } else {
          if (role === ROLES.HEAD) {
            this.headPermissions.update(list => [...list, permission]);
          } else {
            this.specialistPermissions.update(list => [...list, permission]);
          }
          this.toastService.success(`Permission assigned to ${role}`);
        }
        this.saving.set(false);
      },
      error: () => {
        this.toastService.error('Failed to update permission.');
        this.saving.set(false);
      }
    });
  }

  onMemberClick(user: DepartmentMember): void {
    const currentUrl = this.router.url;
    this.router.navigate(['/members', user.userId], {
        queryParams: { returnUrl: currentUrl }
      });
    }

  goBack() {
    this.router.navigate(['/organization/departments']);
  }
}
