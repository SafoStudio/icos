import { Component, inject, computed, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EditableTableComponent, TableColumn } from '../../../shared/components/editable-table/editable-table.component';
import { DepartmentsService } from './departments.service';
import { AuthService } from '../../../core/services/auth.service';
import { Department } from '../../../shared/models/department.model';

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
        console.log('Company ID changed in Departments:', id);
        this.loadDepartments();
      }
    });
  }

  private loadDepartments(): void {
    const companyId = this.companyId();
    if (!companyId) {
      console.warn('No company ID available');
      return;
    }

    console.log('Loading departments for company:', companyId);
    this.loading.set(true);

    this.departmentsService.getDepartments(companyId).subscribe({
      next: (data) => {
        console.log('Departments data received:', data);
        this.departments.set(data);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading departments:', error);
        this.loading.set(false);
      }
    });
  }

  onAddDepartment(item: Department): void {
    const companyId = this.companyId();
    if (!companyId) {
      alert('No company selected');
      return;
    }

    this.loading.set(true);

    this.departmentsService.createDepartment(companyId, item.name, item.description || '').subscribe({
      next: (newDepartment) => {
        console.log('Department created:', newDepartment);
        this.departments.update(departments => [newDepartment, ...departments]);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error creating department:', error);
        alert('Failed to create department');
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
        console.log('Department updated:', updatedDepartment);
        this.departments.update(departments => {
          const updated = [...departments];
          updated[index] = updatedDepartment;
          return updated;
        });
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error updating department:', error);
        alert('Failed to update department');
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
        console.log('Department deleted:', department.id);
        this.departments.update(departments => departments.filter((_, i) => i !== index));
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error deleting department:', error);
        alert('Failed to delete department');
        this.loading.set(false);
      }
    });
  }
}
