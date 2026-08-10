import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { AuthService, ToastService } from '@services';
import { TaskBoardService, TaskItem } from './task-board.service';

export interface TaskStatusLane {
  key: 'TODO' | 'IN_PROGRESS' | 'REVIEW' | 'DONE';
  label: string;
  description: string;
  icon: string;
}

@Component({
  selector: 'app-task-board',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './task-board.component.html',
  styleUrls: ['./task-board.component.scss']
})
export class TaskBoardComponent implements OnInit {
  private readonly taskService = inject(TaskBoardService);
  private readonly toast = inject(ToastService);
  private readonly authService = inject(AuthService);

  readonly defaultDepartmentId = '00000000-0000-0000-0000-000000000010';
  readonly defaultAssigneeId = '00000000-0000-0000-0000-000000000002';

  tasks = signal<TaskItem[]>([]);
  isLoading = signal<boolean>(false);
  searchQuery = signal<string>('');
  selectedStatusFilter = signal<string>('ALL');

  showCreateModal = signal<boolean>(false);
  taskForm = {
    title: '',
    description: '',
    assigneeId: this.defaultAssigneeId,
    departmentId: this.defaultDepartmentId
  };

  readonly lanes: TaskStatusLane[] = [
    { key: 'TODO', label: 'To Do / Backlog', description: 'Pending tasks ready to be started', icon: 'tablerClipboardList' },
    { key: 'IN_PROGRESS', label: 'In Progress', description: 'Currently active work items', icon: 'tablerBulb' },
    { key: 'REVIEW', label: 'In Review', description: 'Under peer review or QA inspection', icon: 'tablerEye' },
    { key: 'DONE', label: 'Completed', description: 'Finished work ready for archive', icon: 'tablerCheck' }
  ];

  filteredTasks = computed(() => {
    let list = this.tasks();
    const query = this.searchQuery().toLowerCase().trim();
    const filterStatus = this.selectedStatusFilter();

    if (filterStatus !== 'ALL') {
      list = list.filter(t => t.status === filterStatus);
    }

    if (query) {
      list = list.filter(t =>
        t.title.toLowerCase().includes(query) ||
        (t.description?.toLowerCase().includes(query))
      );
    }

    return list;
  });

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.isLoading.set(true);
    const user = this.authService.currentUser();
    const assigneeId = user?.id || this.defaultAssigneeId;

    this.taskService.getTasksByAssignee(assigneeId).subscribe({
      next: (res: any) => {
        this.tasks.set(res.tasksByAssignee || []);
        this.isLoading.set(false);
      },
      error: (err: any) => {
        this.toast.error('Failed to load tasks: ' + (err.message || 'Error'));
        this.isLoading.set(false);
      }
    });
  }

  getTasksForLane(laneKey: string): TaskItem[] {
    return this.filteredTasks().filter(t => t.status === laneKey);
  }

  moveTask(task: TaskItem, newStatus: string): void {
    if (task.status === newStatus) return;
    this.taskService.moveTask(task.id, newStatus).subscribe({
      next: (res: any) => {
        this.toast.success(`Task status updated to ${newStatus}`);
        this.loadTasks();
      },
      error: (err: any) => {
        this.toast.error('Failed to move task: ' + (err.message || 'Error'));
      }
    });
  }

  openCreateModal(): void {
    const user = this.authService.currentUser();
    this.taskForm = {
      title: '',
      description: '',
      assigneeId: user?.id || this.defaultAssigneeId,
      departmentId: this.defaultDepartmentId
    };
    this.showCreateModal.set(true);
  }

  closeCreateModal(): void {
    this.showCreateModal.set(false);
  }

  submitCreateTask(): void {
    if (!this.taskForm.title) {
      this.toast.error('Task title is required');
      return;
    }

    this.taskService.createTask(
      this.taskForm.title,
      this.taskForm.description,
      this.taskForm.assigneeId,
      this.taskForm.departmentId
    ).subscribe({
      next: (res: any) => {
        this.toast.success(`Task "${res.createTask.title}" created successfully!`);
        this.closeCreateModal();
        this.loadTasks();
      },
      error: (err: any) => {
        this.toast.error('Failed to create task: ' + (err.message || 'Error'));
      }
    });
  }

  getNextStatus(currentStatus: string): string | null {
    if (currentStatus === 'TODO') return 'IN_PROGRESS';
    if (currentStatus === 'IN_PROGRESS') return 'REVIEW';
    if (currentStatus === 'REVIEW') return 'DONE';
    return null;
  }

  getPrevStatus(currentStatus: string): string | null {
    if (currentStatus === 'DONE') return 'REVIEW';
    if (currentStatus === 'REVIEW') return 'IN_PROGRESS';
    if (currentStatus === 'IN_PROGRESS') return 'TODO';
    return null;
  }

  closeModalOnBackdrop(event: MouseEvent): void {
    const dialog = event.target as HTMLDialogElement;
    if (dialog.tagName === 'DIALOG') {
      this.closeCreateModal();
    }
  }

  onModalKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      this.closeCreateModal();
    }
  }
}
