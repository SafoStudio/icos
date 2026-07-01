import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { ApiService } from '../../core/services/api.service';

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

@Component({
  selector: 'app-members',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './members.component.html',
  styleUrls: ['./members.component.scss']
})
export class MembersComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  readonly authService = inject(AuthService);
  private readonly api = inject(ApiService);

  members = signal<Member[]>([]);
  departments = signal<Department[]>([]);
  isLoading = signal(false);

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
    // HR can invite anyone except ADMIN
    return ['OWNER', 'HEAD', 'SPECIALIST'];
  });

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

    // Load departments
    const deptQuery = `
      query GetDepartments($companyId: ID!) {
        departments(companyId: $companyId) {
          id
          name
          description
        }
      }
    `;

    // Load members
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
        this.departments.set(deptData.departments);

        // Once departments are loaded, load members
        this.api.graphql<{ usersByCompany: Member[] }>(membersQuery, { companyId }).subscribe({
          next: (membersData) => {
            this.isLoading.set(false);
            // Map department names
            const mappedMembers = membersData.usersByCompany.map(m => ({
              ...m,
              departmentName: this.departments().find(d => d.id === m.departmentId)?.name || 'N/A'
            }));
            this.members.set(mappedMembers);
          },
          error: () => this.isLoading.set(false)
        });
      },
      error: () => this.isLoading.set(false)
    });
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
        this.loadData(); // reload list
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
    if (!deptId) return 'N/A';
    return this.departments().find(d => d.id === deptId)?.name || 'N/A';
  }
}
