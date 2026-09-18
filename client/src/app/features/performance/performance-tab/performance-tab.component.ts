import { Component, OnInit, Input, OnChanges, SimpleChanges, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { forkJoin } from 'rxjs';
import { PerformanceService, ToastService } from '@services';
import { Objective, PerformanceReview } from '@models';

@Component({
  selector: 'app-performance-tab',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './performance-tab.component.html',
  styleUrls: ['./performance-tab.component.scss']
})
export class PerformanceTabComponent implements OnInit, OnChanges {
  private readonly performanceService = inject(PerformanceService);
  private readonly toastService = inject(ToastService);

  @Input({ required: true }) userId!: string;
  @Input() canManage = false;
  @Input() isPersonal = false;

  readonly activeSubTab = signal<'objectives' | 'reviews'>('objectives');
  readonly loading = signal<boolean>(true);
  readonly saving = signal<boolean>(false);
  readonly objectives = signal<Objective[]>([]);
  readonly reviews = signal<PerformanceReview[]>([]);

  readonly showObjectiveModal = signal<boolean>(false);
  readonly showReviewModal = signal<boolean>(false);

  // New Objective form fields
  newObjTitle = '';
  newObjDesc = '';
  newObjStartDate = '';
  newObjEndDate = '';

  // New Review form fields
  newReviewRating = 5;
  newReviewSummary = '';

  readonly activeObjectivesCount = computed(() => {
    return this.objectives().filter(o => o.status !== 'COMPLETED').length;
  });

  readonly avgProgress = computed(() => {
    const list = this.objectives();
    if (list.length === 0) return 0;
    const sum = list.reduce((acc, o) => acc + (o.progress || 0), 0);
    return Math.round(sum / list.length);
  });

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

    const obs$ = this.isPersonal
      ? forkJoin({
          objs: this.performanceService.getMyObjectives(),
          revs: this.performanceService.getEmployeeReviews(this.userId)
        })
      : forkJoin({
          objs: this.performanceService.getUserObjectives(this.userId),
          revs: this.performanceService.getEmployeeReviews(this.userId)
        });

    obs$.subscribe({
      next: ({ objs, revs }) => {
        this.objectives.set(objs || []);
        this.reviews.set(revs || []);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error loading performance data:', err);
        this.toastService.error('Failed to load performance information');
        this.loading.set(false);
      }
    });
  }

  openObjectiveModal(): void {
    const today = new Date();
    const future = new Date();
    future.setMonth(future.getMonth() + 3);

    this.newObjTitle = '';
    this.newObjDesc = '';
    this.newObjStartDate = today.toISOString().split('T')[0];
    this.newObjEndDate = future.toISOString().split('T')[0];
    this.showObjectiveModal.set(true);
  }

  closeObjectiveModal(): void {
    this.showObjectiveModal.set(false);
  }

  saveObjective(): void {
    if (!this.newObjTitle.trim()) {
      this.toastService.warning('Please enter an objective title');
      return;
    }
    if (!this.newObjStartDate || !this.newObjEndDate) {
      this.toastService.warning('Please select start and target end dates');
      return;
    }

    this.saving.set(true);
    this.performanceService.createObjective({
      userId: this.isPersonal ? undefined : this.userId,
      title: this.newObjTitle.trim(),
      description: this.newObjDesc.trim(),
      startDate: this.newObjStartDate,
      endDate: this.newObjEndDate
    }).subscribe({
      next: () => {
        this.toastService.success('Objective created successfully!');
        this.saving.set(false);
        this.closeObjectiveModal();
        this.loadData();
      },
      error: (err) => {
        console.error('Error creating objective:', err);
        this.toastService.error(err.message || 'Failed to create objective');
        this.saving.set(false);
      }
    });
  }

  openReviewModal(): void {
    this.newReviewRating = 5;
    this.newReviewSummary = '';
    this.showReviewModal.set(true);
  }

  closeReviewModal(): void {
    this.showReviewModal.set(false);
  }

  saveReview(): void {
    if (!this.newReviewSummary.trim()) {
      this.toastService.warning('Please enter a summary or feedback notes for the review');
      return;
    }

    this.saving.set(true);
    this.performanceService.submitReview({
      employeeId: this.userId,
      rating: this.newReviewRating,
      summary: this.newReviewSummary.trim()
    }).subscribe({
      next: () => {
        this.toastService.success('Performance review submitted and finalized!');
        this.saving.set(false);
        this.closeReviewModal();
        this.loadData();
      },
      error: (err) => {
        console.error('Error submitting review:', err);
        this.toastService.error(err.message || 'Failed to submit review');
        this.saving.set(false);
      }
    });
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

  getRatingLabel(rating: number): string {
    switch (rating) {
      case 5: return '5 - Exceptional / Exceeds Expectations';
      case 4: return '4 - Strongly Meets Expectations';
      case 3: return '3 - Meets Expectations';
      case 2: return '2 - Needs Improvement';
      case 1: return '1 - Unsatisfactory';
      default: return `${rating} Stars`;
    }
  }
}
