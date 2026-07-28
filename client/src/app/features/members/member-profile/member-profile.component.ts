import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { CommonModule, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ProfileService, ProfileUpdateInput, UserUpdateInput } from '../../profile/profile.service';
import { ApiService, AuthService, ToastService } from '@services';
import { type User, type UserProfile, type Department } from '@models';
import { NgIconComponent } from '@ng-icons/core';
import { TabsComponent, type TabItem } from '@components';

@Component({
  selector: 'app-member-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent, TabsComponent],
  templateUrl: './member-profile.component.html',
  styleUrls: ['./member-profile.component.scss']
})
export class MemberProfileComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly location = inject(Location);
  private readonly profileService = inject(ProfileService);
  private readonly api = inject(ApiService);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);

  readonly memberId = signal<string | null>(null);
  readonly loading = signal<boolean>(true);
  readonly saving = signal<boolean>(false);
  readonly member = signal<User | null>(null);
  readonly departments = signal<Department[]>([]);
  readonly isEditing = signal<boolean>(false);
  readonly activeTabId = signal<string>('personal');
  readonly returnUrl = signal<string>('/members');

  readonly rolesList = ['OWNER', 'ADMIN', 'HEAD', 'SPECIALIST'];

  readonly tabs: TabItem[] = [
    { id: 'personal', label: 'Personal & Work Info', icon: 'tablerUser' },
    { id: 'hr_management', label: 'Actions & Roles', icon: 'tablerBuilding' },
    { id: 'compensation', label: 'Compensation', icon: 'tablerCoin' },
    { id: 'documents', label: 'Documents', icon: 'tablerFile' }
  ];

 readonly backButtonText = computed(() => {
    const url = this.returnUrl();
    if (url.includes('/organization/departments/')) {
      return 'Back to Department';
    }
    return 'Back to Members List';
  });

readonly backIcon = computed(() => {
    const url = this.returnUrl();
    if (url.includes('/organization/departments/')) {
      return 'tablerArrowLeft';
    }
    return 'tablerChevronLeft';
  });

  readonly canManage = computed(() => {
    const currentUser = this.authService.currentUser();
    if (!currentUser) return false;
    if (currentUser.role === 'ADMIN' || currentUser.role === 'OWNER') return true;

    const hrDept = this.departments().find(d => d.id === currentUser.departmentId);
    return hrDept?.name?.toUpperCase() === 'HR';
  });

  readonly fullName = computed(() => {
    const m = this.member();
    return m ? `${m.firstName} ${m.lastName}` : '';
  });

  readonly roleLabel = computed(() => {
    const m = this.member();
    if (!m) return '';

    const labels: Record<string, string> = {
      'OWNER': 'Company Owner',
      'ADMIN': 'System Administrator',
      'HEAD': 'Department Head',
      'SPECIALIST': 'Specialist'
    };

    return m.role && labels[m.role] ? labels[m.role] : m.role;
  });

  readonly departmentName = computed(() => {
    const m = this.member();
    if (!m?.departmentId) return 'Unassigned / General';
    const dept = this.departments().find(d => d.id === m.departmentId);
    return dept ? dept.name : 'Unassigned';
  });

  editUser: Partial<User> & { role?: string; departmentId?: string } = {};
  editProfile: Partial<UserProfile> = {};

  selectedRole: string = '';
  selectedDepartmentId: string = '';

  ngOnInit(): void {
    const returnUrl = this.route.snapshot.queryParams['returnUrl'];
    if (returnUrl) {
      this.returnUrl.set(returnUrl);
    }

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.memberId.set(id);
        this.loadData(id);
      } else {
        this.router.navigate(['/members']);
      }
    });
  }

 goBack(): void {
    const url = this.returnUrl();
    if (url) {
      this.router.navigateByUrl(url);
    } else {
      this.location.back();
    }
  }

  loadData(id: string): void {
    this.loading.set(true);

    const currentUser = this.authService.currentUser();
    const companyId = currentUser?.companyId;

    const deptQuery = `
      query GetDepartments($companyId: ID!) {
        departments(companyId: $companyId) {
          id
          name
          description
        }
      }
    `;

    if (companyId) {
      this.api.graphql<{ departments: Department[] }>(deptQuery, { companyId }).subscribe({
        next: (res) => {
          this.departments.set(res.departments || []);
          this.fetchMember(id);
        },
        error: () => {
          this.fetchMember(id);
        }
      });
    } else {
      this.fetchMember(id);
    }
  }

  private fetchMember(id: string): void {
    this.profileService.getUserById(id).subscribe({
      next: (user) => {
        this.member.set(user);
        this.selectedRole = user.role || 'SPECIALIST';
        this.selectedDepartmentId = user.departmentId || '';
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error fetching member profile:', err);
        this.toastService.error('Failed to load employee profile');
        this.loading.set(false);
      }
    });
  }

  startEdit(): void {
    const m = this.member();
    if (!m) return;

    this.editUser = {
      firstName: m.firstName,
      lastName: m.lastName,
      role: m.role,
      departmentId: m.departmentId || ''
    };

    this.editProfile = {
      phoneNumber: m.profile?.phoneNumber || '',
      address: m.profile?.address || '',
      birthDate: m.profile?.birthDate || '',
      hireDate: m.profile?.hireDate || ''
    };

    this.isEditing.set(true);
  }

  cancelEdit(): void {
    this.isEditing.set(false);
    this.editUser = {};
    this.editProfile = {};
  }

  saveProfile(): void {
    const m = this.member();
    if (!m) return;

    this.saving.set(true);

    const userUpdate: UserUpdateInput = {};
    if (this.editUser.firstName && this.editUser.firstName !== m.firstName) {
      userUpdate.firstName = this.editUser.firstName;
    }
    if (this.editUser.lastName && this.editUser.lastName !== m.lastName) {
      userUpdate.lastName = this.editUser.lastName;
    }
    if (this.editUser.role && this.editUser.role !== m.role) {
      userUpdate.role = this.editUser.role;
    }
    if (this.editUser.departmentId !== undefined && this.editUser.departmentId !== m.departmentId) {
      userUpdate.departmentId = this.editUser.departmentId;
    }

    const profileUpdate: ProfileUpdateInput = {
      userId: m.id
    };

    if (this.editProfile.phoneNumber !== m.profile?.phoneNumber) {
      profileUpdate.phoneNumber = this.editProfile.phoneNumber;
    }
    if (this.editProfile.address !== m.profile?.address) {
      profileUpdate.address = this.editProfile.address;
    }
    if (this.editProfile.birthDate !== m.profile?.birthDate) {
      profileUpdate.birthDate = this.editProfile.birthDate;
    }
    if (this.editProfile.hireDate !== m.profile?.hireDate) {
      profileUpdate.hireDate = this.editProfile.hireDate;
    }

    const updates: Promise<any>[] = [];

    if (Object.keys(userUpdate).length > 0) {
       updates.push(firstValueFrom(this.profileService.updateUser(m.id, userUpdate)));
    }

    if (Object.keys(profileUpdate).length > 1) {
      updates.push(firstValueFrom(this.profileService.updateProfile(profileUpdate)));
    }

    Promise.all(updates)
      .then(() => {
        this.toastService.success('Employee profile updated successfully');
        this.isEditing.set(false);
        this.fetchMember(m.id);
        this.saving.set(false);
      })
      .catch((err) => {
        console.error('Error updating member:', err);
        this.toastService.error(err.message || 'Failed to update employee profile');
        this.saving.set(false);
      });
  }

  applyRoleChange(): void {
    const m = this.member();
    if (!m || !this.selectedRole) return;

    if (this.selectedRole === m.role) {
      this.toastService.info('Selected role is identical to current role');
      return;
    }

    this.saving.set(true);
    this.profileService.updateUser(m.id, { role: this.selectedRole }).subscribe({
      next: () => {
        this.toastService.success(`Role updated to ${this.selectedRole} successfully!`);
        this.fetchMember(m.id);
        this.saving.set(false);
      },
      error: (err) => {
        this.toastService.error(err.message || 'Failed to change role');
        this.saving.set(false);
      }
    });
  }

  applyDepartmentChange(): void {
    const m = this.member();
    if (!m) return;

    if (this.selectedDepartmentId === (m.departmentId || '')) {
      this.toastService.info('Employee is already in this department');
      return;
    }

    this.saving.set(true);
    this.profileService.updateUser(m.id, { departmentId: this.selectedDepartmentId }).subscribe({
      next: () => {
        const targetDept = this.departments().find(d => d.id === this.selectedDepartmentId);
        const deptName = targetDept ? targetDept.name : 'Unassigned';
        this.toastService.success(`Employee transferred to ${deptName} department!`);
        this.fetchMember(m.id);
        this.saving.set(false);
      },
      error: (err) => {
        this.toastService.error(err.message || 'Failed to reassign department');
        this.saving.set(false);
      }
    });
  }

  formatDate(date?: string): string {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}
