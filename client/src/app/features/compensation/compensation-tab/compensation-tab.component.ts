import { Component, OnInit, Input, OnChanges, SimpleChanges, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { forkJoin } from 'rxjs';
import { CompensationService, ToastService } from '@services';
import { Compensation, SalaryRevision } from '@models';

@Component({
  selector: 'app-compensation-tab',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './compensation-tab.component.html',
  styleUrls: ['./compensation-tab.component.scss']
})
export class CompensationTabComponent implements OnInit, OnChanges {
  private readonly compensationService = inject(CompensationService);
  private readonly toastService = inject(ToastService);

  @Input({ required: true }) userId!: string;
  @Input() canManage = false;
  @Input() isPersonal = false;

  readonly loading = signal<boolean>(true);
  readonly saving = signal<boolean>(false);
  readonly compensation = signal<Compensation | null>(null);
  readonly salaryHistory = signal<SalaryRevision[]>([]);
  readonly showUpdateModal = signal<boolean>(false);

  newSalary: number | null = null;
  effectiveDate = '';
  reason = '';

  ngOnInit(): void {
    if (this.userId) {
      this.loadData();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['userId'] && !changes['userId'].firstChange && this.userId) {
      this.loadData();
    }
  }

  loadData(): void {
    if (!this.userId) return;
    this.loading.set(true);

    forkJoin({
      comp: this.compensationService.getCompensation(this.userId),
      history: this.compensationService.getSalaryHistory(this.userId)
    }).subscribe({
      next: ({ comp, history }) => {
        this.compensation.set(comp);
        this.salaryHistory.set(history || []);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error loading compensation:', err);
        this.toastService.error('Failed to load compensation data');
        this.loading.set(false);
      }
    });
  }

  openModal(): void {
    const comp = this.compensation();
    this.newSalary = comp?.baseSalary ?? null;
    this.effectiveDate = new Date().toISOString().split('T')[0];
    this.reason = '';
    this.showUpdateModal.set(true);
  }

  closeModal(): void {
    this.showUpdateModal.set(false);
    this.newSalary = null;
    this.reason = '';
  }

  saveSalary(): void {
    if (!this.userId) return;

    if (this.newSalary === null || this.newSalary === undefined || this.newSalary <= 0) {
      this.toastService.warning('Please specify a valid positive salary amount');
      return;
    }

    if (!this.effectiveDate) {
      this.toastService.warning('Please select an effective date');
      return;
    }

    this.saving.set(true);

    this.compensationService.updateSalary({
      userId: this.userId,
      newSalary: Number(this.newSalary),
      reason: this.reason.trim() || 'Salary adjustment',
      effectiveDate: this.effectiveDate
    }).subscribe({
      next: () => {
        this.toastService.success('Salary updated successfully!');
        this.saving.set(false);
        this.closeModal();
        this.loadData();
      },
      error: (err) => {
        console.error('Error updating salary:', err);
        this.toastService.error(err.message || 'Failed to update salary');
        this.saving.set(false);
      }
    });
  }

  formatCurrency(amount?: number, currency = 'USD'): string {
    if (amount === undefined || amount === null) return '—';
    try {
      return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: currency || 'USD',
        maximumFractionDigits: 0
      }).format(amount);
    } catch {
      return `$${amount.toLocaleString()}`;
    }
  }

  formatDate(dateStr?: string): string {
    if (!dateStr) return '—';
    try {
      return new Date(dateStr).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    } catch {
      return dateStr;
    }
  }

  getCurrencySymbol(currency = 'USD'): string {
    switch ((currency || 'USD').toUpperCase()) {
      case 'EUR': return '€';
      case 'GBP': return '£';
      case 'UAH': return '₴';
      default: return '$';
    }
  }

  calcDelta(previous?: number, current?: number): { type: string; text: string } {
    if (!previous || previous <= 0 || !current) {
      return { type: 'neutral', text: '0%' };
    }
    const delta = current - previous;
    const pct = ((delta / previous) * 100).toFixed(1);
    if (delta > 0) {
      return { type: 'positive', text: `+${pct}%` };
    } else if (delta < 0) {
      return { type: 'negative', text: `${pct}%` };
    }
    return { type: 'neutral', text: '0.0%' };
  }
}
