import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { AuthService, ToastService } from '@services';
import {
  RecruitmentBoardService,
  Vacancy,
  Application,
  KanbanBoard,
  KanbanColumn,
  CandidateInput,
  CreateVacancyInput,
} from './recruitment-board.service';

@Component({
  selector: 'app-recruitment-board',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './recruitment-board.component.html',
  styleUrls: ['./recruitment-board.component.scss']
})
export class RecruitmentBoardComponent implements OnInit {
  private readonly recruitmentService = inject(RecruitmentBoardService);
  private readonly toast = inject(ToastService);
  private readonly authService = inject(AuthService);

  public companyId = signal<string>('');
  public vacancies = signal<Vacancy[]>([]);
  public selectedVacancyId = signal<string>('');
  public board = signal<KanbanBoard | null>(null);
  public isLoading = signal<boolean>(false);
  public searchQuery = signal<string>('');

  public showApplyModal = signal<boolean>(false);
  public showVacancyModal = signal<boolean>(false);
  public selectedApplication = signal<Application | null>(null);

  public candidateForm: CandidateInput = {
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    source: 'LinkedIn',
    resumeUrl: ''
  };

  public vacancyForm: Partial<CreateVacancyInput> = {
    title: '',
    description: '',
    requirements: ''
  };

  public hasCompany = computed<boolean>(() => {
    return !!this.companyId();
  });

  public selectedVacancy = computed(() => {
    return this.vacancies().find(v => v.id === this.selectedVacancyId()) || null;
  });

  public filteredColumns = computed<KanbanColumn[]>(() => {
    const currentBoard = this.board();
    if (!currentBoard) return [];

    const query = this.searchQuery().toLowerCase().trim();
    if (!query) return currentBoard.columns;

    return currentBoard.columns.map(col => ({
      ...col,
      applications: col.applications.filter(app =>
        `${app.candidate.firstName} ${app.candidate.lastName}`.toLowerCase().includes(query) ||
        app.candidate.email.toLowerCase().includes(query) ||
        (app.candidate.source && app.candidate.source.toLowerCase().includes(query))
      )
    }));
  });

  public ngOnInit(): void {
    this.loadCompanyId();
  }

  public loadCompanyId(): void {
    const user = this.authService.currentUser();
    if (user?.companyId) {
      this.companyId.set(user.companyId);
      this.loadVacancies();
    } else {
      this.toast.error('Company not found for current user');
    }
  }

  public loadVacancies(): void {
    const companyId = this.companyId();
    if (!companyId) {
      this.toast.error('Company ID is required');
      return;
    }

    this.isLoading.set(true);
    this.recruitmentService.getVacancies(companyId).subscribe({
      next: (res) => {
        const list = res.vacancies || [];
        this.vacancies.set(list);
        if (list.length > 0) {
          this.selectedVacancyId.set(list[0].id);
          this.loadKanbanBoard(list[0].id);
        } else {
          this.isLoading.set(false);
        }
      },
      error: (err) => {
        this.toast.error('Failed to load vacancies: ' + (err.message || 'Error'));
        this.isLoading.set(false);
      }
    });
  }

  public onVacancyChange(vacancyId: string): void {
    this.selectedVacancyId.set(vacancyId);
    this.loadKanbanBoard(vacancyId);
  }

  public loadKanbanBoard(vacancyId: string): void {
    if (!vacancyId) return;
    this.isLoading.set(true);
    this.recruitmentService.getKanbanBoard(vacancyId).subscribe({
      next: (res) => {
        this.board.set(res.kanbanBoard);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.toast.error('Failed to load recruitment board: ' + (err.message || 'Error'));
        this.isLoading.set(false);
      }
    });
  }

  public moveApplication(app: Application, targetStageId: string): void {
    if (!targetStageId || app.currentStageId === targetStageId) return;
    this.recruitmentService.moveApplication(app.id, targetStageId).subscribe({
      next: () => {
        this.toast.success(`Moved candidate ${app.candidate.firstName} to new stage`);
        this.loadKanbanBoard(this.selectedVacancyId());
      },
      error: (err) => {
        this.toast.error('Failed to move application: ' + (err.message || 'Error'));
      }
    });
  }

  public hireCandidate(app: Application): void {
    const user = this.authService.currentUser();
    const userId = user?.id || '';

    if (!userId) {
      this.toast.error('User not authenticated');
      return;
    }

    this.recruitmentService.hireCandidate(app.id, userId).subscribe({
      next: () => {
        this.toast.success(`Candidate ${app.candidate.firstName} ${app.candidate.lastName} hired successfully!`);
        this.loadKanbanBoard(this.selectedVacancyId());
      },
      error: (err) => {
        this.toast.error('Failed to hire candidate: ' + (err.message || 'Error'));
      }
    });
  }

  public openApplyModal(): void {
    this.candidateForm = {
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      source: 'LinkedIn',
      resumeUrl: ''
    };
    this.showApplyModal.set(true);
  }

  public closeApplyModal(): void {
    this.showApplyModal.set(false);
  }

  public submitApply(): void {
    const vId = this.selectedVacancyId();
    if (!vId) {
      this.toast.error('Please select a vacancy first');
      return;
    }
    if (!this.candidateForm.firstName || !this.candidateForm.lastName || !this.candidateForm.email) {
      this.toast.error('First Name, Last Name and Email are required');
      return;
    }

    this.recruitmentService.applyToVacancy(vId, this.candidateForm).subscribe({
      next: () => {
        this.toast.success('Candidate registered successfully!');
        this.closeApplyModal();
        this.loadKanbanBoard(vId);
      },
      error: (err) => {
        this.toast.error('Failed to register candidate: ' + (err.message || 'Error'));
      }
    });
  }

  public openVacancyModal(): void {
    this.vacancyForm = {
      title: '',
      description: '',
      requirements: ''
    };
    this.showVacancyModal.set(true);
  }

  public closeVacancyModal(): void {
    this.showVacancyModal.set(false);
  }

  public submitVacancy(): void {
    const companyId = this.companyId();
    if (!companyId) {
      this.toast.error('Company not found');
      return;
    }

    if (!this.vacancyForm.title) {
      this.toast.error('Vacancy title is required');
      return;
    }

    const payload: CreateVacancyInput = {
      companyId: companyId,
      title: this.vacancyForm.title,
      description: this.vacancyForm.description || '',
      requirements: this.vacancyForm.requirements || ''
    };

    this.recruitmentService.createVacancy(payload).subscribe({
      next: (res) => {
        this.toast.success(`Vacancy "${res.createVacancy.title}" created successfully!`);
        this.closeVacancyModal();
        this.loadVacancies();
      },
      error: (err) => {
        this.toast.error('Failed to create vacancy: ' + (err.message || 'Error'));
      }
    });
  }

  public openCandidateDetail(app: Application): void {
    this.selectedApplication.set(app);
  }

  public closeCandidateDetail(): void {
    this.selectedApplication.set(null);
  }

  public getInitials(firstName: string, lastName: string): string {
    return `${firstName ? firstName.charAt(0) : ''}${lastName ? lastName.charAt(0) : ''}`.toUpperCase();
  }

  public closeModalOnBackdrop(event: MouseEvent, modalType: 'apply' | 'vacancy' | 'detail'): void {
    const dialog = event.target as HTMLDialogElement;
    if (dialog.tagName === 'DIALOG') {
      if (modalType === 'apply') this.closeApplyModal();
      else if (modalType === 'vacancy') this.closeVacancyModal();
      else if (modalType === 'detail') this.closeCandidateDetail();
    }
  }

  public onModalKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      if (this.showApplyModal()) {
        this.closeApplyModal();
      } else if (this.showVacancyModal()) {
        this.closeVacancyModal();
      } else if (this.selectedApplication()) {
        this.closeCandidateDetail();
      }
    }
  }
}
