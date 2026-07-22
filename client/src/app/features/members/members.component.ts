import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ApiService } from '../../core/services/api.service';
import { ToastService } from '../../core/services/toast.service';
import { NgIconComponent } from '@ng-icons/core';

interface Department {
  id: string;
  name: string;
  description?: string;
}

interface Member {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  departmentId?: string;
  departmentName?: string;
  status: string;
}

export type ViewMode = 'grid' | 'list';
export type SortOption = 'name-asc' | 'name-desc' | 'role' | 'dept';

@Component({
  selector: 'app-members',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, NgIconComponent],
  templateUrl: './members.component.html',
  styleUrls: ['./members.component.scss']
})
export class MembersComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  readonly authService = inject(AuthService);
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);

  members = signal<Member[]>([]);
  departments = signal<Department[]>([]);
  isLoading = signal(false);
  viewMode = signal<ViewMode>('grid');
  // Filter & Search controls
  searchQuery = signal<string>('');
  selectedDepartmentFilter = signal<string>('');
  selectedRoleFilter = signal<string>('');
  sortBy = signal<SortOption>('name-asc');
  // Invite modal states
  showInviteModal = signal(false);
  isInviting = signal(false);
  inviteError = signal<string | null>(null);
  inviteSuccess = signal<string | null>(null);
  inviteForm!: FormGroup;

  canInvite = computed(() => {
    const user = this.authService.currentUser();
    if (!user) return false;
    if (user.role === 'ADMIN' || user.role === 'OWNER') return true;

    // Check if user is in HR department
    const userDept = this.departments().find(d => d.id === user.departmentId);
    return userDept?.name?.toUpperCase() === 'HR';
  });

  availableRoles = computed(() => {
    const user = this.authService.currentUser();
    if (user?.role === 'ADMIN' || user?.role === 'OWNER') {
      return ['ADMIN', 'OWNER', 'HEAD', 'SPECIALIST'];
    }
    return ['OWNER', 'HEAD', 'SPECIALIST'];
  });

  // Filtered & Sorted members calculation
  filteredMembers = computed(() => {
    let list = this.members();
    const query = this.searchQuery().trim().toLowerCase();
    const deptId = this.selectedDepartmentFilter();
    const role = this.selectedRoleFilter();
    const sort = this.sortBy();

    if (query) {
      list = list.filter(m => {
        const fullName = `${m.firstName} ${m.lastName}`.toLowerCase();
        const email = m.email.toLowerCase();
        return fullName.includes(query) || email.includes(query);
      });
    }

    if (deptId) {
      if (deptId === 'unassigned') {
        list = list.filter(m => !m.departmentId);
      } else {
        list = list.filter(m => m.departmentId === deptId);
      }
    }

    if (role) {
      list = list.filter(m => m.role === role);
    }

    // Sorting
    return [...list].sort((a, b) => {
      if (sort === 'name-asc') {
        return `${a.firstName} ${a.lastName}`.localeCompare(`${b.firstName} ${b.lastName}`);
      } else if (sort === 'name-desc') {
        return `${b.firstName} ${b.lastName}`.localeCompare(`${a.firstName} ${a.lastName}`);
      } else if (sort === 'role') {
        return a.role.localeCompare(b.role);
      } else if (sort === 'dept') {
        return (a.departmentName || '').localeCompare(b.departmentName || '');
      }
      return 0;
    });
  });

  // Statistics counters
  totalCount = computed(() => this.members().length);
  activeCount = computed(() => this.members().filter(m => m.status === 'ACTIVE' || !m.status).length);

  ngOnInit(): void {
    this.initInviteForm();
    this.loadData();
  }

  private initInviteForm(): void {
    this.inviteForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      role: ['SPECIALIST', [Validators.required]],
      departmentId: ['', [Validators.required]],
      managerId: ['']
    });
  }

  loadData(): void {
    const user = this.authService.currentUser();
    if (!user) return;

    this.isLoading.set(true);
    const companyId = user.companyId;

    const deptQuery = `
      query GetDepartments($companyId: ID!) {
        departments(companyId: $companyId) {
          id
          name
          description
        }
      }
    `;

    const membersQuery = `
      query UsersByCompany($companyId: ID!) {
        usersByCompany(companyId: $companyId) {
          id
          firstName
          lastName
          email
          role
          departmentId
          status
        }
      }
    `;

    this.api.graphql<{ departments: Department[] }>(deptQuery, { companyId }).subscribe({
      next: (deptData) => {
        this.departments.set(deptData.departments || []);

        this.api.graphql<{ usersByCompany: Member[] }>(membersQuery, { companyId }).subscribe({
          next: (membersData) => {
            this.isLoading.set(false);
            const mappedMembers = (membersData.usersByCompany || []).map(m => ({
              ...m,
              departmentName: this.departments().find(d => d.id === m.departmentId)?.name || 'Unassigned'
            }));
            this.members.set(mappedMembers);
          },
          error: (err) => {
            console.error('Failed to load members:', err);
            this.isLoading.set(false);
          }
        });
      },
      error: (err) => {
        console.error('Failed to load departments:', err);
        this.isLoading.set(false);
      }
    });
  }

  setViewMode(mode: ViewMode): void {
    this.viewMode.set(mode);
  }

  clearFilters(): void {
    this.searchQuery.set('');
    this.selectedDepartmentFilter.set('');
    this.selectedRoleFilter.set('');
    this.sortBy.set('name-asc');
  }

  navigateToProfile(memberId: string): void {
    this.router.navigate(['/members', memberId]);
  }

  openInviteModal(): void {
    if (!this.canInvite()) return;
    this.inviteForm.reset({
      email: '',
      role: 'SPECIALIST',
      departmentId: this.departments().length > 0 ? this.departments()[0].id : '',
      managerId: ''
    });
    this.inviteError.set(null);
    this.inviteSuccess.set(null);
    this.showInviteModal.set(true);
  }

  closeInviteModal(): void {
    this.showInviteModal.set(false);
  }

  submitInvite(): void {
    if (this.inviteForm.invalid) {
      this.inviteForm.markAllAsTouched();
      return;
    }

    this.isInviting.set(true);
    this.inviteError.set(null);
    this.inviteSuccess.set(null);

    const formValue = this.inviteForm.value;
    const input = {
      email: formValue.email,
      role: formValue.role,
      departmentId: formValue.departmentId,
      managerId: formValue.managerId || null
    };

    const mutation = `
      mutation CreateInvite($input: InviteRequestInput!) {
        createInvite(input: $input) {
          id
          email
          role
          status
        }
      }
    `;

    this.api.graphql<{ createInvite: any }>(mutation, { input }).subscribe({
      next: () => {
        this.isInviting.set(false);
        this.inviteSuccess.set('Invitation sent successfully!');
        this.toastService.success('Invitation sent successfully!');
        this.loadData();
        setTimeout(() => {
          this.closeInviteModal();
        }, 1500);
      },
      error: (err) => {
        this.isInviting.set(false);
        this.inviteError.set(err.message || 'Failed to send invitation.');
      }
    });
  }

  getDepartmentName(deptId: string | undefined): string {
    if (!deptId) return 'Unassigned';
    return this.departments().find(d => d.id === deptId)?.name || 'Unassigned';
  }

  getRoleLabel(role: string): string {
    const labels: Record<string, string> = {
      'OWNER': 'Owner',
      'ADMIN': 'Admin',
      'HEAD': 'Dept Head',
      'SPECIALIST': 'Specialist'
    };
    return labels[role] || role;
  }

  getMemberPhotoUrl(member: Member): string {
    const photos = [
      'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600&auto=format&fit=crop&q=80',
      'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600&auto=format&fit=crop&q=80',
      'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=600&auto=format&fit=crop&q=80',
      'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=600&auto=format&fit=crop&q=80',
      'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=600&auto=format&fit=crop&q=80',
      'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=600&auto=format&fit=crop&q=80',
      'https://images.unsplash.com/photo-1560250097-0b93528c311a?w=600&auto=format&fit=crop&q=80',
      'https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?w=600&auto=format&fit=crop&q=80',
      'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=600&auto=format&fit=crop&q=80',
      'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=600&auto=format&fit=crop&q=80'
    ];

    let hash = 0;
    const str = member.id || member.email || member.firstName;
    for (let i = 0; i < str.length; i++) {
      hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    const index = Math.abs(hash) % photos.length;
    return photos[index];
  }
}
