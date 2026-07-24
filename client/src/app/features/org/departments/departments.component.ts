import { Component, inject, computed, effect, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { EditableTableComponent, TableColumn } from '@shared/components/editable-table/editable-table.component';
import { DepartmentsService } from './departments.service';
import { AuthService,ToastService } from '@services';
import { type Department } from '@models';

@Component({
  selector: 'app-departments',
  standalone: true,
  imports: [CommonModule, EditableTableComponent],
  templateUrl: './departments.component.html',
  styleUrls: ['./departments.component.scss']
})
export class DepartmentsComponent {
  private readonly authService = inject(AuthService);
  private readonly departmentsService = inject(DepartmentsService);
  private readonly toastService = inject(ToastService);
  private readonly router = inject(Router);

  readonly companyId = computed(() => this.authService.currentUser()?.companyId || null);
  readonly departments = signal<Department[]>([]);
  readonly loading = signal<boolean>(false);

  readonly columns: TableColumn[] = [
    { key: 'name', label: 'Name', required: true },
    { key: 'description', label: 'Description', required: false }
  ];

  constructor() {
    effect(() => {
      const id = this.companyId();
      if (id) {
        this.loadDepartments();
      }
    });
  }

  private loadDepartments(): void {
    const companyId = this.companyId();
    if (!companyId) {
      return;
    }

    this.loading.set(true);

    this.departmentsService.getDepartments(companyId).subscribe({
      next: (data) => {
        this.departments.set(data);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading departments:', error);
        this.toastService.error('Failed to load departments.');
        this.loading.set(false);
      }
    });
  }

  onRowClick(department: Department): void {
    this.router.navigate(['/organization/departments', department.id]);
  }

  onAddDepartment(item: Department): void {
    const companyId = this.companyId();
    if (!companyId) {
      this.toastService.error('No company selected.');
      return;
    }

    this.loading.set(true);

    this.departmentsService.createDepartment(companyId, item.name, item.description || '').subscribe({
      next: (newDepartment) => {
        this.departments.update(departments => [newDepartment, ...departments]);
        this.toastService.success(`Department "${newDepartment.name}" created successfully.`);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error creating department:', error);
        this.toastService.error('Failed to create department.');
        this.loading.set(false);
        this.loadDepartments();
      }
    });
  }

  onUpdateDepartment(event: { index: number; item: Department }): void {
    const { index, item } = event;
    this.loading.set(true);

    this.departmentsService.updateDepartment(item.id, item.name, item.description || '').subscribe({
      next: (updatedDepartment) => {
        this.departments.update(departments => {
          const updated = [...departments];
          updated[index] = updatedDepartment;
          return updated;
        });
        this.toastService.success(`Department "${updatedDepartment.name}" updated successfully.`);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error updating department:', error);
        this.toastService.error('Failed to update department.');
        this.loading.set(false);
        this.loadDepartments();
      }
    });
  }

  onDeleteDepartment(index: number): void {
    const department = this.departments()[index];
    this.loading.set(true);

    this.departmentsService.deleteDepartment(department.id).subscribe({
      next: () => {
        this.departments.update(departments => departments.filter((_, i) => i !== index));
        this.toastService.success(`Department "${department.name}" deleted successfully.`);
        this.loading.set(false);
      },
      error: (error: any) => {
        console.error('Error deleting department:', error);
        this.toastService.error('Failed to delete department. It might be in use.');
        this.loading.set(false);
      }
    });
  }
}
