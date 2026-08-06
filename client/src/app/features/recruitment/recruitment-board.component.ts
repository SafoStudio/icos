import { Component, OnInit, signal, computed, inject } from '@angular/core';
 import { CommonModule } from '@angular/common';
 import { FormsModule } from '@angular/forms';
 import { NgIconComponent } from '@ng-icons/core';
 import { ToastService } from '@services';
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

   readonly companyId = '00000000-0000-0000-0000-000000000001';

   vacancies = signal<Vacancy[]>([]);
   selectedVacancyId = signal<string>('');
   board = signal<KanbanBoard | null>(null);
   isLoading = signal<boolean>(false);
   searchQuery = signal<string>('');

   showApplyModal = signal<boolean>(false);
   showVacancyModal = signal<boolean>(false);
   selectedApplication = signal<Application | null>(null);

   candidateForm: CandidateInput = {
     firstName: '',
     lastName: '',
     email: '',
     phone: '',
     source: 'LinkedIn',
     resumeUrl: ''
   };

   vacancyForm: Partial<CreateVacancyInput> = {
     title: '',
     description: '',
     requirements: ''
   };

   selectedVacancy = computed(() => {
     return this.vacancies().find(v => v.id === this.selectedVacancyId()) || null;
   });

   // ✅ Явная типизация с использованием KanbanColumn
   filteredColumns = computed<KanbanColumn[]>(() => {
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

   ngOnInit(): void {
     this.loadVacancies();
   }

   loadVacancies(): void {
     this.isLoading.set(true);
     this.recruitmentService.getVacancies(this.companyId).subscribe({
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

   onVacancyChange(vacancyId: string): void {
     this.selectedVacancyId.set(vacancyId);
     this.loadKanbanBoard(vacancyId);
   }

   loadKanbanBoard(vacancyId: string): void {
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

   moveApplication(app: Application, targetStageId: string): void {
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

   hireCandidate(app: Application): void {
     const dummyUserId = '00000000-0000-0000-0000-000000000002';
     this.recruitmentService.hireCandidate(app.id, dummyUserId).subscribe({
       next: () => {
         this.toast.success(`Candidate ${app.candidate.firstName} ${app.candidate.lastName} hired successfully!`);
         this.loadKanbanBoard(this.selectedVacancyId());
       },
       error: (err) => {
         this.toast.error('Failed to hire candidate: ' + (err.message || 'Error'));
       }
     });
   }

   openApplyModal(): void {
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

   closeApplyModal(): void {
     this.showApplyModal.set(false);
   }

   submitApply(): void {
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

   openVacancyModal(): void {
     this.vacancyForm = {
       title: '',
       description: '',
       requirements: ''
     };
     this.showVacancyModal.set(true);
   }

   closeVacancyModal(): void {
     this.showVacancyModal.set(false);
   }

   submitVacancy(): void {
     if (!this.vacancyForm.title) {
       this.toast.error('Vacancy title is required');
       return;
     }

     const payload: CreateVacancyInput = {
       companyId: this.companyId,
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

   openCandidateDetail(app: Application): void {
     this.selectedApplication.set(app);
   }

   closeCandidateDetail(): void {
     this.selectedApplication.set(null);
   }

   getInitials(firstName: string, lastName: string): string {
     return `${firstName ? firstName.charAt(0) : ''}${lastName ? lastName.charAt(0) : ''}`.toUpperCase();
   }

 closeModalOnBackdrop(event: MouseEvent, modalType: 'apply' | 'vacancy' | 'detail'): void {
     const dialog = event.target as HTMLDialogElement;
     if (dialog.tagName === 'DIALOG') {
       if (modalType === 'apply') this.closeApplyModal();
       else if (modalType === 'vacancy') this.closeVacancyModal();
       else if (modalType === 'detail') this.closeCandidateDetail();
     }
   }

   onModalKeydown(event: KeyboardEvent): void {
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
