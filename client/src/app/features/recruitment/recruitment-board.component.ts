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

  public showApplySidebar = signal<boolean>(false);
  public showVacancySidebar = signal<boolean>(false);
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
        this.closeCandidateDetail();
      },
      error: (err) => {
        this.toast.error('Failed to hire candidate: ' + (err.message || 'Error'));
      }
    });
  }

  public openApplySidebar(): void {
    this.closeVacancySidebar();
    this.closeCandidateDetail();
    this.candidateForm = {
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      source: 'LinkedIn',
      resumeUrl: ''
    };
    this.showApplySidebar.set(true);
  }

  public closeApplySidebar(): void {
    this.showApplySidebar.set(false);
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
        this.closeApplySidebar();
        this.loadKanbanBoard(vId);
      },
      error: (err) => {
        this.toast.error('Failed to register candidate: ' + (err.message || 'Error'));
      }
    });
  }

  public openVacancySidebar(): void {
    this.closeApplySidebar();
    this.closeCandidateDetail();
    this.vacancyForm = {
      title: '',
      description: '',
      requirements: ''
    };
    this.showVacancySidebar.set(true);
  }

  public closeVacancySidebar(): void {
    this.showVacancySidebar.set(false);
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
        this.closeVacancySidebar();
        this.loadVacancies();
      },
      error: (err) => {
        this.toast.error('Failed to create vacancy: ' + (err.message || 'Error'));
      }
    });
  }

  public openCandidateDetail(app: Application): void {
    this.closeApplySidebar();
    this.closeVacancySidebar();
    this.selectedApplication.set(app);
  }

  public closeCandidateDetail(): void {
    this.selectedApplication.set(null);
  }

  public getInitials(firstName: string, lastName: string): string {
    return `${firstName ? firstName.charAt(0) : ''}${lastName ? lastName.charAt(0) : ''}`.toUpperCase();
  }

  public closeAllSidebars(): void {
    this.closeApplySidebar();
    this.closeVacancySidebar();
    this.closeCandidateDetail();
  }

  public onOverlayClick(): void {
    this.closeAllSidebars();
  }

  public onModalKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      this.closeAllSidebars();
    }
  }
}
