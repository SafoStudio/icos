import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { AuthService, ToastService } from '@services';
import { MemberService, Member } from '../../members/member.service';
import {
  OffboardingService,
  OffboardingProcess,
  OffboardingProcessStatus,
  OffboardingTemplate,
  OffboardingTask,
  StartOffboardingInput
} from './offboarding.service';

export interface OffboardingColumn {
  status: OffboardingProcessStatus;
  label: string;
  badgeClass: string;
  processes: OffboardingProcess[];
}

@Component({
  selector: 'app-offboarding-pipeline',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './offboarding-pipeline.component.html',
  styleUrls: ['./offboarding-pipeline.component.scss']
})
export class OffboardingPipelineComponent implements OnInit {
  private readonly offboardingService = inject(OffboardingService);
  private readonly memberService = inject(MemberService);
  private readonly authService = inject(AuthService);
  private readonly toast = inject(ToastService);

  public companyId = signal<string>('');
  public processes = signal<OffboardingProcess[]>([]);
  public templates = signal<OffboardingTemplate[]>([]);
  public members = signal<Member[]>([]);
  public isLoading = signal<boolean>(false);
  public searchQuery = signal<string>('');

  public showStartModal = signal<boolean>(false);
  public isSubmitting = signal<boolean>(false);
  public expandedProcessId = signal<string | null>(null);

  public startForm: { membershipId: string; templateId: string; lastWorkingDay: string; reason: string; initiatedBy: string } = {
    membershipId: '',
    templateId: '',
    lastWorkingDay: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString().substring(0, 10),
    reason: 'Resignation',
    initiatedBy: ''
  };

  public readonly stageFlow: OffboardingProcessStatus[] = [
    'PENDING_APPROVAL',
    'HR_INTERVIEW',
    'KNOWLEDGE_TRANSFER',
    'IT_REVOKE',
    'FINANCIAL_SETTLEMENT',
    'READY_FOR_SIGN_OFF',
    'COMPLETED'
  ];

  public columns = computed<OffboardingColumn[]>(() => {
    const list = this.processes();
    const query = this.searchQuery().toLowerCase().trim();

    const filtered = list.filter(p => {
      if (!query) return true;
      const mem = this.getMember(p.membershipId);
      const name = mem ? `${mem.firstName} ${mem.lastName}`.toLowerCase() : '';
      const email = mem ? mem.email.toLowerCase() : '';
      const reason = p.reason ? p.reason.toLowerCase() : '';
      return name.includes(query) || email.includes(query) || reason.includes(query) || p.status.toLowerCase().includes(query);
    });

    return [
      {
        status: 'PENDING_APPROVAL',
        label: 'Approval',
        badgeClass: 'badge-approval',
        processes: filtered.filter(p => p.status === 'PENDING_APPROVAL')
      },
      {
        status: 'HR_INTERVIEW',
        label: 'HR Interview',
        badgeClass: 'badge-interview',
        processes: filtered.filter(p => p.status === 'HR_INTERVIEW')
      },
      {
        status: 'KNOWLEDGE_TRANSFER',
        label: 'Handover',
        badgeClass: 'badge-transfer',
        processes: filtered.filter(p => p.status === 'KNOWLEDGE_TRANSFER')
      },
      {
        status: 'IT_REVOKE',
        label: 'IT Revoke',
        badgeClass: 'badge-it',
        processes: filtered.filter(p => p.status === 'IT_REVOKE')
      },
      {
        status: 'FINANCIAL_SETTLEMENT',
        label: 'Settlement',
        badgeClass: 'badge-finance',
        processes: filtered.filter(p => p.status === 'FINANCIAL_SETTLEMENT')
      },
      {
        status: 'READY_FOR_SIGN_OFF',
        label: 'Sign-Off',
        badgeClass: 'badge-signoff',
        processes: filtered.filter(p => p.status === 'READY_FOR_SIGN_OFF')
      },
      {
        status: 'COMPLETED',
        label: 'Departed',
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

    this.offboardingService.getTemplates(cId).subscribe({
      next: res => this.templates.set(res.offboardingTemplates || []),
      error: () => {}
    });

    this.memberService.getMembers(cId).subscribe({
      next: res => this.members.set(res || []),
      error: () => {}
    });

    this.offboardingService.getProcessesByCompany(cId).subscribe({
      next: res => {
        this.processes.set(res.offboardingProcessesByCompany || []);
        this.isLoading.set(false);
      },
      error: err => {
        this.toast.error('Failed to load offboarding processes: ' + (err.message || 'Error'));
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

  public getCompletedTasksCount(tasks: OffboardingTask[]): number {
    if (!tasks) return 0;
    return tasks.filter(t => t.status === 'DONE' || t.status === 'SKIPPED').length;
  }

  public getProgressPercentage(tasks: OffboardingTask[]): number {
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

  public approveProcess(processId: string): void {
    this.offboardingService.approveProcess(processId).subscribe({
      next: () => {
        this.toast.success('Offboarding process approved');
        this.loadData();
      },
      error: err => this.toast.error('Approval failed: ' + (err.message || 'Error'))
    });
  }

  public signOffProcess(processId: string): void {
    this.offboardingService.signOffProcess(processId).subscribe({
      next: () => {
        this.toast.success('Offboarding signed off and completed');
        this.loadData();
      },
      error: err => this.toast.error('Sign-off failed: ' + (err.message || 'Error'))
    });
  }

  public advanceStage(proc: OffboardingProcess): void {
    const currIdx = this.stageFlow.indexOf(proc.status);
    if (currIdx >= 0 && currIdx < this.stageFlow.length - 1) {
      const nextStatus = this.stageFlow[currIdx + 1];
      this.transitionProcess(proc.id, nextStatus);
    }
  }

  public transitionProcess(processId: string, status: OffboardingProcessStatus): void {
    this.offboardingService.transitionProcess({ processId, status }).subscribe({
      next: () => {
        this.toast.success(`Transitioned to ${status}`);
        this.loadData();
      },
      error: err => this.toast.error('Transition failed: ' + (err.message || 'Error'))
    });
  }

  public cancelProcess(processId: string): void {
    if (!confirm('Are you sure you want to cancel this offboarding process?')) return;
    this.offboardingService.cancelProcess(processId).subscribe({
      next: () => {
        this.toast.success('Offboarding cancelled');
        this.loadData();
      },
      error: err => this.toast.error('Cancel failed: ' + (err.message || 'Error'))
    });
  }

  public completeTask(task: OffboardingTask, event: Event): void {
    event.stopPropagation();
    if (task.status === 'DONE') return;

    this.offboardingService.completeTask(task.id).subscribe({
      next: () => {
        this.toast.success(`Task "${task.title}" completed`);
        this.loadData();
      },
      error: err => this.toast.error('Task error: ' + (err.message || 'Error'))
    });
  }

  public skipTask(task: OffboardingTask, event: Event): void {
    event.stopPropagation();
    this.offboardingService.skipTask(task.id).subscribe({
      next: () => {
        this.toast.success(`Task "${task.title}" skipped`);
        this.loadData();
      },
      error: err => this.toast.error('Skip failed: ' + (err.message || 'Error'))
    });
  }

  public openStartModal(): void {
    const user = this.authService.currentUser();
    const tmpls = this.templates();
    this.startForm = {
      membershipId: this.members().length > 0 ? this.members()[0].id : '',
      templateId: tmpls.length > 0 ? tmpls[0].id : '',
      lastWorkingDay: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString().substring(0, 10),
      reason: 'Resignation',
      initiatedBy: user ? `${user.firstName} ${user.lastName}` : 'HR Team'
    };
    this.showStartModal.set(true);
  }

  public closeStartModal(): void {
    this.showStartModal.set(false);
  }

  public submitStartOffboarding(): void {
    if (!this.startForm.membershipId || !this.startForm.templateId) {
      this.toast.error('Employee and Template are required');
      return;
    }

    this.isSubmitting.set(true);
    const input: StartOffboardingInput = {
      membershipId: this.startForm.membershipId,
      templateId: this.startForm.templateId,
      lastWorkingDay: this.startForm.lastWorkingDay,
      reason: this.startForm.reason,
      initiatedBy: this.startForm.initiatedBy || 'HR'
    };

    this.offboardingService.startOffboarding(input).subscribe({
      next: () => {
        this.toast.success('Offboarding process initiated successfully');
        this.isSubmitting.set(false);
        this.closeStartModal();
        this.loadData();
      },
      error: err => {
        this.toast.error('Failed to start offboarding: ' + (err.message || 'Error'));
        this.isSubmitting.set(false);
      }
    });
  }
}
