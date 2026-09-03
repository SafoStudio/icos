import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { AuthService, ToastService } from '@services';
import { MemberService, Member } from '../../members/member.service';
import {
  OnboardingService,
  OnboardingProcess,
  OnboardingProcessStatus,
  OnboardingTemplate,
  OnboardingTask,
  StartOnboardingInput
} from './onboarding.service';

export interface OnboardingColumn {
  status: OnboardingProcessStatus;
  label: string;
  badgeClass: string;
  processes: OnboardingProcess[];
}

@Component({
  selector: 'app-onboarding-pipeline',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './onboarding-pipeline.component.html',
  styleUrls: ['./onboarding-pipeline.component.scss']
})
export class OnboardingPipelineComponent implements OnInit {
  private readonly onboardingService = inject(OnboardingService);
  private readonly memberService = inject(MemberService);
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);

  public companyId = signal<string>('');
  public processes = signal<OnboardingProcess[]>([]);
  public templates = signal<OnboardingTemplate[]>([]);
  public members = signal<Member[]>([]);
  public isLoading = signal<boolean>(false);
  public searchQuery = signal<string>('');

  // Modal / drawer state
  public showStartModal = signal<boolean>(false);
  public isSubmitting = signal<boolean>(false);
  public expandedProcessId = signal<string | null>(null);

  public startForm: { membershipId: string; templateId: string; startDate: string } = {
    membershipId: '',
    templateId: '',
    startDate: new Date().toISOString().substring(0, 10)
  };

  public columns = computed<OnboardingColumn[]>(() => {
    const list = this.processes();
    const query = this.searchQuery().toLowerCase().trim();

    const filtered = list.filter(p => {
      if (!query) return true;
      const mem = this.getMember(p.membershipId);
      const name = mem ? `${mem.firstName} ${mem.lastName}`.toLowerCase() : '';
      const email = mem ? mem.email.toLowerCase() : '';
      return name.includes(query) || email.includes(query) || p.status.toLowerCase().includes(query);
    });

    return [
      {
        status: 'NOT_STARTED',
        label: 'Not Started',
        badgeClass: 'badge-not-started',
        processes: filtered.filter(p => p.status === 'NOT_STARTED')
      },
      {
        status: 'IN_PROGRESS',
        label: 'In Progress',
        badgeClass: 'badge-in-progress',
        processes: filtered.filter(p => p.status === 'IN_PROGRESS')
      },
      {
        status: 'STALLED',
        label: 'Stalled',
        badgeClass: 'badge-stalled',
        processes: filtered.filter(p => p.status === 'STALLED')
      },
      {
        status: 'COMPLETED',
        label: 'Completed',
        badgeClass: 'badge-completed',
        processes: filtered.filter(p => p.status === 'COMPLETED')
      }
    ];
  });

  public ngOnInit(): void {
    const user = this.authService.currentUser();
    if (user?.companyId) {
      this.companyId.set(user.companyId);
      this.loadData();
    } else {
      this.toast.error('Company ID not found for current user');
    }
  }

  public loadData(): void {
    const cId = this.companyId();
    if (!cId) return;

    this.isLoading.set(true);

    // Load templates
    this.onboardingService.getTemplates(cId).subscribe({
      next: res => this.templates.set(res.onboardingTemplates || []),
      error: () => {}
    });

    // Load company members for employee info
    this.memberService.getMembers(cId).subscribe({
      next: res => this.members.set(res || []),
      error: () => {}
    });

    // Load onboarding processes
    this.onboardingService.getProcessesByCompany(cId).subscribe({
      next: res => {
        this.processes.set(res.onboardingProcessesByCompany || []);
        this.isLoading.set(false);
      },
      error: err => {
        this.toast.error('Failed to load onboarding processes: ' + (err.message || 'Error'));
        this.isLoading.set(false);
      }
    });
  }

  public getMember(membershipId: string): Member | undefined {
    return this.members().find(m => m.id === membershipId);
  }

  public getMemberName(membershipId: string): string {
    const m = this.getMember(membershipId);
    return m ? `${m.firstName} ${m.lastName}` : `Employee (${membershipId.substring(0, 8)})`;
  }

  public getMemberEmail(membershipId: string): string {
    const m = this.getMember(membershipId);
    return m ? m.email : '';
  }

  public getInitials(membershipId: string): string {
    const m = this.getMember(membershipId);
    if (!m) return 'EM';
    const first = m.firstName ? m.firstName.charAt(0) : '';
    const last = m.lastName ? m.lastName.charAt(0) : '';
    return (first + last).toUpperCase();
  }

  public getCompletedTasksCount(tasks: OnboardingTask[]): number {
    if (!tasks) return 0;
    return tasks.filter(t => t.status === 'DONE').length;
  }

  public getProgressPercentage(tasks: OnboardingTask[]): number {
    if (!tasks || tasks.length === 0) return 0;
    const completed = this.getCompletedTasksCount(tasks);
    return Math.round((completed / tasks.length) * 100);
  }

  public toggleProcessTasks(processId: string): void {
    if (this.expandedProcessId() === processId) {
      this.expandedProcessId.set(null);
    } else {
      this.expandedProcessId.set(processId);
    }
  }

  public completeTask(task: OnboardingTask, event: Event): void {
    event.stopPropagation();
    if (task.status === 'DONE') return;

    this.onboardingService.completeTask(task.id).subscribe({
      next: () => {
        this.toast.success(`Task "${task.title}" completed`);
        this.loadData();
      },
      error: err => {
        this.toast.error('Failed to complete task: ' + (err.message || 'Error'));
      }
    });
  }

  public openStartModal(): void {
    const tmpls = this.templates();
    this.startForm = {
      membershipId: this.members().length > 0 ? this.members()[0].id : '',
      templateId: tmpls.length > 0 ? tmpls[0].id : '',
      startDate: new Date().toISOString().substring(0, 10)
    };
    this.showStartModal.set(true);
  }

  public closeStartModal(): void {
    this.showStartModal.set(false);
  }

  public submitStartOnboarding(): void {
    if (!this.startForm.membershipId || !this.startForm.templateId) {
      this.toast.error('Employee and Template are required');
      return;
    }

    this.isSubmitting.set(true);
    const input: StartOnboardingInput = {
      membershipId: this.startForm.membershipId,
      templateId: this.startForm.templateId,
      startDate: this.startForm.startDate
    };

    this.onboardingService.startOnboarding(input).subscribe({
      next: () => {
        this.toast.success('Onboarding process initiated successfully');
        this.isSubmitting.set(false);
        this.closeStartModal();
        this.loadData();
      },
      error: err => {
        this.toast.error('Failed to start onboarding: ' + (err.message || 'Error'));
        this.isSubmitting.set(false);
      }
    });
  }
}
