import { Component, inject, OnInit, signal, computed, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, ToastService } from '@services';
import { NgIconComponent } from '@ng-icons/core';
import { PaginationComponent } from '@components';
import { MemberInviteComponent } from './member-invite/member-invite.component';
import { MemberService, Member } from './member.service';
import { MembersStateService } from './members-state.service';
import { type Department, getRoleShortLabel, ALL_ROLES, type UserRole, ROLES } from '@models';

export type ViewMode = 'grid' | 'list';
export type SortOption = 'name-asc' | 'name-desc' | 'role' | 'dept';

@Component({
  selector: 'app-members',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    NgIconComponent,
    PaginationComponent,
    MemberInviteComponent
  ],
  templateUrl: './members.component.html',
  styleUrls: ['./members.component.scss']
})
export class MembersComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly memberService = inject(MemberService);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);
  private readonly stateService = inject(MembersStateService);

  members = signal<Member[]>([]);
  departments = signal<Department[]>([]);
  isLoading = signal(false);

  viewMode = signal<ViewMode>('list');
  searchQuery = signal<string>('');
  selectedDepartmentFilter = signal<string>('');
  selectedRoleFilter = signal<string>('');
  sortBy = signal<SortOption>('name-asc');
  currentPage = signal<number>(1);
  pageSize = signal<number>(12);
  readonly pageSizeOptions = [6, 12, 24, 48];

  showInviteModal = signal(false);
  isInviting = signal(false);
  inviteError = signal<string | null>(null);
  inviteSuccess = signal<string | null>(null);

  constructor() {
    const savedState = this.stateService.getState();
    this.viewMode.set(savedState().viewMode);
    this.searchQuery.set(savedState().searchQuery);
    this.selectedDepartmentFilter.set(savedState().selectedDepartmentFilter);
    this.selectedRoleFilter.set(savedState().selectedRoleFilter);
    this.sortBy.set(savedState().sortBy);
    this.currentPage.set(savedState().currentPage || 1);
    this.pageSize.set(savedState().pageSize || 12);

    effect(() => {
      this.stateService.setViewMode(this.viewMode());
    });

    effect(() => {
      this.stateService.setSearchQuery(this.searchQuery());
    });

    effect(() => {
      this.stateService.setDepartmentFilter(this.selectedDepartmentFilter());
    });

    effect(() => {
      this.stateService.setRoleFilter(this.selectedRoleFilter());
    });

    effect(() => {
      this.stateService.setSortBy(this.sortBy());
    });

    effect(() => {
      this.stateService.setCurrentPage(this.currentPage());
    });

    effect(() => {
      this.stateService.setPageSize(this.pageSize());
    });

    // Keep currentPage in valid bounds when filtered list changes
    effect(() => {
      const totalPages = Math.max(1, Math.ceil(this.filteredMembers().length / this.pageSize()));
      if (this.currentPage() > totalPages) {
        this.currentPage.set(totalPages);
      }
    });
  }

  canInvite = computed(() => {
    const user = this.authService.currentUser();
    if (!user) return false;
    if (user.role === 'ADMIN' || user.role === 'OWNER') return true;
    const userDept = this.departments().find(d => d.id === user.departmentId);
    return userDept?.name?.toUpperCase() === 'HR';
  });

  availableRoles = computed(() => {
    const user = this.authService.currentUser();
    if (user?.role === 'ADMIN' || user?.role === 'OWNER') {
      return ALL_ROLES;
    }
    return [ROLES.OWNER, ROLES.HEAD, ROLES.SPECIALIST];
  });

  filteredMembers = computed(() => {
    const list = this.applyFilters(this.members());
    return this.applySorting(list);
  });

  paginatedMembers = computed(() => {
    const list = this.filteredMembers();
    const page = this.currentPage();
    const size = this.pageSize();
    const start = (page - 1) * size;
    return list.slice(start, start + size);
  });

  totalCount = computed(() => this.members().length);
  activeCount = computed(() => this.members().filter(m => m.status === 'ACTIVE' || !m.status).length);


  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    const user = this.authService.currentUser();
    if (!user) return;

    this.isLoading.set(true);
    const companyId = user.companyId;

    this.memberService.getDepartments(companyId).subscribe({
      next: (depts) => {
        this.departments.set(depts);
        this.loadMembers(companyId);
      },
      error: (err) => {
        console.error('Failed to load departments:', err);
        this.isLoading.set(false);
      }
    });
  }

  private loadMembers(companyId: string): void {
    this.memberService.getMembers(companyId).subscribe({
      next: (members) => {
        this.isLoading.set(false);
        const mapped = members.map(m => ({
          ...m,
          departmentName: this.getDepartmentName(m.departmentId)
        }));
        this.members.set(mapped);
      },
      error: (err) => {
        console.error('Failed to load members:', err);
        this.isLoading.set(false);
      }
    });
  }

  private applyFilters(list: Member[]): Member[] {
    const query = this.searchQuery().trim().toLowerCase();
    const deptId = this.selectedDepartmentFilter();
    const role = this.selectedRoleFilter();

    let filtered = list;

    if (query) {
      filtered = filtered.filter(m => {
        const fullName = `${m.firstName} ${m.lastName}`.toLowerCase();
        const email = m.email.toLowerCase();
        return fullName.includes(query) || email.includes(query);
      });
    }

    if (deptId) {
      if (deptId === 'unassigned') {
        filtered = filtered.filter(m => !m.departmentId);
      } else {
        filtered = filtered.filter(m => m.departmentId === deptId);
      }
    }

    if (role) {
      filtered = filtered.filter(m => m.role === role);
    }

    return filtered;
  }

  private applySorting(list: Member[]): Member[] {
    const sort = this.sortBy();
    return [...list].sort((a, b) => {
      switch (sort) {
        case 'name-asc':
          return `${a.firstName} ${a.lastName}`.localeCompare(`${b.firstName} ${b.lastName}`);
        case 'name-desc':
          return `${b.firstName} ${b.lastName}`.localeCompare(`${a.firstName} ${a.lastName}`);
        case 'role':
          return a.role.localeCompare(b.role);
        case 'dept':
          return (a.departmentName || '').localeCompare(b.departmentName || '');
        default:
          return 0;
      }
    });
  }

  private getDepartmentName(deptId: string | undefined): string {
    if (!deptId) return 'Unassigned';
    return this.departments().find(d => d.id === deptId)?.name || 'Unassigned';
  }

  setViewMode(mode: ViewMode): void {
    this.viewMode.set(mode);
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedDepartmentFilter.set('');
    this.selectedRoleFilter.set('');
    this.sortBy.set('name-asc');
    this.currentPage.set(1);
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
  }

  onPageSizeChange(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(1);
  }


  navigateToProfile(memberId: string): void {
    this.router.navigate(['/members', memberId], {
      queryParams: { returnUrl: '/members' }
    });
  }

  onMemberClick(member: Member): void {
    this.router.navigate(['/members', member.id], {
      queryParams: { returnUrl: '/members' }
    });
  }

  openInviteModal(): void {
    if (!this.canInvite()) return;
    this.inviteError.set(null);
    this.inviteSuccess.set(null);
    this.showInviteModal.set(true);
  }

  closeInviteModal(): void {
    this.showInviteModal.set(false);
  }

  handleInviteSubmit(formValue: any): void {
    this.isInviting.set(true);
    this.inviteError.set(null);
    this.inviteSuccess.set(null);

    const input = {
      email: formValue.email,
      role: formValue.role,
      departmentId: formValue.departmentId,
      managerId: formValue.managerId || null
    };

    this.memberService.createInvite(input).subscribe({
      next: () => {
        this.isInviting.set(false);
        this.inviteSuccess.set('Invitation sent successfully!');
        this.toastService.success('Invitation sent successfully!');
        this.loadData();
        setTimeout(() => this.closeInviteModal(), 1500);
      },
      error: (err) => {
        this.isInviting.set(false);
        this.inviteError.set(err.message || 'Failed to send invitation.');
      }
    });
  }

  getRoleLabel(role: string): string {
    return getRoleShortLabel(role as UserRole) || role;
  }

  getAvatarUrl(member: Member): string | null {
    if (member.profile?.avatarPath) {
      if (member.profile.avatarPath.startsWith('http')) {
        return member.profile.avatarPath;
      }
       return `/api/storage/avatar/${member.id}`;
    }
    return null;
  }
}
