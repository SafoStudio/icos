import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { AuthService, ToastService } from '@services';
import { TaskBoardService, TaskItem } from './task-board.service';
import { type DepartmentMember } from '@models';

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

  tasks = signal<TaskItem[]>([]);
  departmentMembers = signal<DepartmentMember[]>([]);
  isLoading = signal<boolean>(false);
  isCreating = signal<boolean>(false);
  searchQuery = signal<string>('');
  selectedStatusFilter = signal<string>('ALL');
  showCreateModal = signal<boolean>(false);

  taskForm = {
    title: '',
    description: '',
    assigneeId: ''
  };

  readonly lanes: TaskStatusLane[] = [
    { key: 'TODO', label: 'To Do / Backlog', description: 'Pending tasks ready to be started', icon: 'tablerClipboardList' },
    { key: 'IN_PROGRESS', label: 'In Progress', description: 'Currently active work items', icon: 'tablerBulb' },
    { key: 'REVIEW', label: 'In Review', description: 'Under peer review or QA inspection', icon: 'tablerEye' },
    { key: 'DONE', label: 'Completed', description: 'Finished work ready for archive', icon: 'tablerCheck' }
  ];

  hasDepartment = computed(() => {
    const user = this.authService.currentUser();
    return !!user?.departmentId;
  });

  currentUser = computed(() => {
    return this.authService.currentUser();
  });

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

  getUserName = computed(() => {
    const userMap = new Map<string, string>();
    this.departmentMembers().forEach(member => {
      userMap.set(member.userId, `${member.firstName} ${member.lastName}`);
    });
    return (userId: string | null) => {
      if (!userId) return 'Unassigned';
      return userMap.get(userId) || userId.substring(0, 8) + '...';
    };
  });

  ngOnInit(): void {
    this.loadDepartmentMembers();
    this.loadTasks();
  }

  loadDepartmentMembers(): void {
    const user = this.authService.currentUser();
    if (!user?.departmentId) {
      this.departmentMembers.set([]);
      return;
    }

    this.taskService.getDepartmentMembers(user.departmentId).subscribe({
      next: (members) => {
        this.departmentMembers.set(members);
      },
      error: (err) => {
        console.error('Failed to load department members:', err);
        this.toast.error('Failed to load team members');
      }
    });
  }

  loadTasks(): void {
    this.isLoading.set(true);
    const user = this.authService.currentUser();

    if (!user?.id) {
      this.toast.error('User not authenticated');
      this.isLoading.set(false);
      return;
    }

    if (this.hasDepartment()) {
      this.loadDepartmentTasks(user.departmentId!);
    } else {
      this.loadPersonalTasks(user.id);
    }
  }

  loadDepartmentTasks(departmentId: string): void {
    this.taskService.getTasksByDepartment(departmentId).subscribe({
      next: (res: any) => {
        this.tasks.set(res.tasksByDepartment || []);
        this.isLoading.set(false);
      },
      error: (err: any) => {
        this.toast.error('Failed to load tasks: ' + (err.message || 'Error'));
        this.isLoading.set(false);
      }
    });
  }

  loadPersonalTasks(assigneeId: string): void {
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
      next: () => {
        this.toast.success(`Task moved to ${newStatus}`);
        this.loadTasks();
      },
      error: (err: any) => {
        this.toast.error('Failed to move task: ' + (err.message || 'Error'));
      }
    });
  }

  openCreateModal(): void {
    this.taskForm = {
      title: '',
      description: '',
      assigneeId: ''
    };
    this.showCreateModal.set(true);
  }

  closeCreateModal(): void {
    this.showCreateModal.set(false);
    this.isCreating.set(false);
  }

  submitCreateTask(): void {
    if (!this.taskForm.title?.trim()) {
      this.toast.error('Task title is required');
      return;
    }

    const user = this.authService.currentUser();
    if (!user?.id) {
      this.toast.error('User not authenticated');
      return;
    }

    this.isCreating.set(true);

    const assigneeId = this.taskForm.assigneeId || user.id;
    const departmentId = user.departmentId || null;

    this.taskService.createTask(
      this.taskForm.title,
      this.taskForm.description,
      assigneeId,
      departmentId
    ).subscribe({
      next: (res: any) => {
        this.toast.success(`Task "${res.createTask.title}" created successfully!`);
        this.closeCreateModal();
        this.loadTasks();
        this.isCreating.set(false);
      },
      error: (err: any) => {
        this.toast.error('Failed to create task: ' + (err.message || 'Error'));
        this.isCreating.set(false);
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
