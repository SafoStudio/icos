import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { forkJoin } from 'rxjs';
import { TimeoffService, ToastService, AuthService } from '@services';
import { LeaveType, LeaveBalance, LeaveRequest, ROLES } from '@models';

@Component({
  selector: 'app-timeoff',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './timeoff.component.html',
  styleUrls: ['./timeoff.component.scss']
})
export class TimeoffComponent implements OnInit {
  private readonly timeoffService = inject(TimeoffService);
  private readonly toastService = inject(ToastService);
  private readonly authService = inject(AuthService);

  readonly currentUser = computed(() => this.authService.currentUser());
  readonly loading = signal<boolean>(true);
  readonly saving = signal<boolean>(false);

  readonly leaveTypes = signal<LeaveType[]>([]);
  readonly balances = signal<LeaveBalance[]>([]);
  readonly requests = signal<LeaveRequest[]>([]);

  readonly showRequestModal = signal<boolean>(false);

  // Form fields
  selectedLeaveTypeId = '';
  requestStartDate = '';
  requestEndDate = '';
  requestReason = '';
  calculatedDays = signal<number>(0);

  readonly canManage = computed(() => {
    const user = this.currentUser();
    if (!user) return false;
    return user.role === ROLES.ADMIN || user.role === ROLES.OWNER || user.role === ROLES.HEAD;
  });

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading.set(true);

    forkJoin({
      types: this.timeoffService.getLeaveTypes(),
      bals: this.timeoffService.getMyLeaveBalances(),
      reqs: this.timeoffService.getMyLeaveRequests()
    }).subscribe({
      next: ({ types, bals, reqs }) => {
        this.leaveTypes.set(types || []);
        this.balances.set(bals || []);
        this.requests.set(reqs || []);
        if (types && types.length > 0 && !this.selectedLeaveTypeId) {
          this.selectedLeaveTypeId = types[0].id;
        }
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error loading timeoff data:', err);
        this.toastService.error('Failed to load leave records');
        this.loading.set(false);
      }
    });
  }

  getLeaveType(typeId: string): LeaveType | undefined {
    return this.leaveTypes().find(t => t.id === typeId);
  }

  calcUsedPercent(used?: number, total?: number): number {
    if (!total || total <= 0) return 0;
    return Math.min(100, Math.round(((used || 0) / total) * 100));
  }

  formatDate(dateStr?: string): string {
    if (!dateStr) return '—';
    try {
      return new Date(dateStr).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
      });
    } catch {
      return dateStr;
    }
  }

  openRequestModal(): void {
    const today = new Date().toISOString().split('T')[0];
    this.requestStartDate = today;
    this.requestEndDate = today;
    this.requestReason = '';
    this.onDateChange();
    this.showRequestModal.set(true);
  }

  closeRequestModal(): void {
    this.showRequestModal.set(false);
  }

  onDateChange(): void {
    if (!this.requestStartDate || !this.requestEndDate) {
      this.calculatedDays.set(0);
      return;
    }
    const start = new Date(this.requestStartDate);
    const end = new Date(this.requestEndDate);
    if (end < start) {
      this.calculatedDays.set(0);
      return;
    }
    const diffTime = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
    this.calculatedDays.set(diffDays);
  }

  submitRequest(): void {
    if (!this.selectedLeaveTypeId) {
      this.toastService.warning('Please select a leave type');
      return;
    }
    if (!this.requestStartDate || !this.requestEndDate) {
      this.toastService.warning('Please select start and end dates');
      return;
    }
    if (this.calculatedDays() <= 0) {
      this.toastService.warning('End date cannot be earlier than start date');
      return;
    }

    this.saving.set(true);
    this.timeoffService.createLeaveRequest({
      leaveTypeId: this.selectedLeaveTypeId,
      startDate: this.requestStartDate,
      endDate: this.requestEndDate,
      reason: this.requestReason.trim()
    }).subscribe({
      next: () => {
        this.toastService.success('Leave request submitted successfully!');
        this.saving.set(false);
        this.closeRequestModal();
        this.loadData();
      },
      error: (err) => {
        console.error('Error submitting leave request:', err);
        this.toastService.error(err.message || 'Failed to submit leave request');
        this.saving.set(false);
      }
    });
  }

  approve(requestId: string): void {
    this.timeoffService.approveLeaveRequest(requestId).subscribe({
      next: () => {
        this.toastService.success('Leave request approved');
        this.loadData();
      },
      error: (err) => {
        this.toastService.error(err.message || 'Failed to approve leave request');
      }
    });
  }

  reject(requestId: string): void {
    const comment = prompt('Enter rejection reason:') || 'Rejected by manager';
    this.timeoffService.rejectLeaveRequest(requestId, comment).subscribe({
      next: () => {
        this.toastService.info('Leave request rejected');
        this.loadData();
      },
      error: (err) => {
        this.toastService.error(err.message || 'Failed to reject leave request');
      }
    });
  }

  cancel(requestId: string): void {
    if (!confirm('Are you sure you want to cancel this leave request?')) return;
    this.timeoffService.cancelLeaveRequest(requestId).subscribe({
      next: () => {
        this.toastService.info('Leave request cancelled');
        this.loadData();
      },
      error: (err) => {
        this.toastService.error(err.message || 'Failed to cancel leave request');
      }
    });
  }
}
