import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { CustomFieldsService, ToastService } from '@services';
import { CustomFieldDefinition, EntityType, FieldType } from '@models';
import { CustomFieldsFormService } from './custom-fields-form.service';
import { CustomFieldsHeaderComponent } from './components/custom-fields-header/custom-fields-header.component';
import { CustomFieldsStatsComponent } from './components/custom-fields-stats/custom-fields-stats.component';
import { CustomFieldsToolbarComponent } from './components/custom-fields-toolbar/custom-fields-toolbar.component';
import { CustomFieldsTableComponent } from './components/custom-fields-table/custom-fields-table.component';
import { CustomFieldEditModalComponent } from './components/custom-field-edit-modal/custom-field-edit-modal.component';
import { CustomFieldDeleteModalComponent } from './components/custom-field-delete-modal/custom-field-delete-modal.component';
import { CustomFieldPlaygroundModalComponent } from './components/custom-field-playground-modal/custom-field-playground-modal.component';

@Component({
  selector: 'app-custom-fields',
  standalone: true,
  imports: [
    CommonModule, FormsModule, NgIconComponent,
    CustomFieldsHeaderComponent,
    CustomFieldsStatsComponent,
    CustomFieldsToolbarComponent,
    CustomFieldsTableComponent,
    CustomFieldEditModalComponent,
    CustomFieldDeleteModalComponent,
    CustomFieldPlaygroundModalComponent
  ],
  providers: [CustomFieldsFormService],
  templateUrl: './custom-fields.component.html',
  styleUrls: ['./custom-fields.component.scss']
})
export class CustomFieldsComponent implements OnInit {
  private readonly customFieldsService = inject(CustomFieldsService);
  private readonly toastService = inject(ToastService);
  readonly formService = inject(CustomFieldsFormService);

  readonly loading = signal<boolean>(true);
  readonly saving = signal<boolean>(false);
  readonly definitions = signal<CustomFieldDefinition[]>([]);
  readonly selectedEntityType = signal<EntityType | 'ALL'>('ALL');
  readonly searchQuery = signal<string>('');
  readonly filterFieldType = signal<FieldType | 'ALL'>('ALL');
  readonly showEditModal = signal<boolean>(false);
  readonly showDeleteModal = signal<boolean>(false);
  readonly showPlaygroundModal = signal<boolean>(false);
  readonly fieldToDelete = signal<CustomFieldDefinition | null>(null);

  readonly totalFieldsCount = computed(() => this.definitions().length);
  readonly entityStats = computed(() => {
    const map: Record<string, number> = {};
    for (const def of this.definitions()) {
      map[def.entityType] = (map[def.entityType] || 0) + 1;
    }
    return map;
  });
  readonly conditionalRulesCount = computed(() => {
    return this.definitions().filter(d => !!d.conditionalFieldKey).length;
  });

  readonly filteredDefinitions = computed(() => {
    let list = this.definitions();
    const entity = this.selectedEntityType();
    const search = this.searchQuery().trim().toLowerCase();
    const type = this.filterFieldType();

    if (entity !== 'ALL') {
      list = list.filter(d => d.entityType === entity);
    }
    if (type !== 'ALL') {
      list = list.filter(d => d.fieldType === type);
    }
    if (search) {
      list = list.filter(d =>
        d.label.toLowerCase().includes(search) ||
        d.key.toLowerCase().includes(search) ||
        (d.description && d.description.toLowerCase().includes(search))
      );
    }
    return list;
  });

  readonly potentialParentFields = computed(() => {
    return this.definitions().filter(
      d => d.entityType === this.formService.entityType() && d.key !== this.formService.key()
    );
  });

  ngOnInit(): void {
    this.loadDefinitions();
  }

  loadDefinitions(): void {
    this.loading.set(true);
    this.customFieldsService.getAllDefinitions().subscribe({
      next: (defs) => {
        this.definitions.set(defs);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error loading custom field definitions:', err);
        this.toastService.error('Failed to load custom field definitions');
        this.loading.set(false);
      }
    });
  }

  openCreateModal(entityType?: EntityType): void {
    this.formService.resetForm();
    if (entityType) {
      this.formService.entityType.set(entityType);
    } else if (this.selectedEntityType() !== 'ALL') {
      this.formService.entityType.set(this.selectedEntityType() as EntityType);
    }
    this.showEditModal.set(true);
  }

  openEditModal(def: CustomFieldDefinition): void {
    this.formService.populateFromDefinition(def);
    this.showEditModal.set(true);
  }

  closeEditModal(): void {
    this.showEditModal.set(false);
  }

  saveDefinition(): void {
    const input = this.formService.getFormInput();
    if (!input.label.trim()) {
      this.toastService.error('Please enter a field label');
      return;
    }
    if (!input.key.trim()) {
      this.toastService.error('Please enter a field key identifier');
      return;
    }
    if ((input.fieldType === 'ENUM' || input.fieldType === 'MULTI_SELECT') && (!input.options || input.options.length === 0)) {
      this.toastService.error('Please add at least one option for select fields');
      return;
    }

    this.saving.set(true);
    const isEdit = !!this.formService.editingId();

    if (isEdit) {
      this.customFieldsService.updateDefinition(this.formService.editingId()!, input).subscribe({
        next: () => {
          this.saving.set(false);
          this.showEditModal.set(false);
          this.toastService.success(`Custom field "${input.label}" updated successfully!`);
          this.loadDefinitions();
        },
        error: (err) => {
          console.error('Error updating definition:', err);
          this.saving.set(false);
          this.toastService.error(err.message || 'Failed to update custom field');
        }
      });
    } else {
      this.customFieldsService.createDefinition(input).subscribe({
        next: () => {
          this.saving.set(false);
          this.showEditModal.set(false);
          this.toastService.success(`Custom field "${input.label}" created successfully!`);
          this.loadDefinitions();
        },
        error: (err) => {
          console.error('Error creating definition:', err);
          this.saving.set(false);
          this.toastService.error(err.message || 'Failed to create custom field');
        }
      });
    }
  }

  confirmDelete(def: CustomFieldDefinition): void {
    this.fieldToDelete.set(def);
    this.showDeleteModal.set(true);
  }

  deleteDefinition(): void {
    const def = this.fieldToDelete();
    if (!def) return;
    this.saving.set(true);
    this.customFieldsService.deleteDefinition(def.id).subscribe({
      next: () => {
        this.saving.set(false);
        this.showDeleteModal.set(false);
        this.fieldToDelete.set(null);
        this.toastService.success(`Custom field "${def.label}" removed successfully`);
        this.loadDefinitions();
      },
      error: (err) => {
        console.error('Error deleting custom field:', err);
        this.saving.set(false);
        this.toastService.error(err.message || 'Failed to delete custom field');
      }
    });
  }

  openPlayground(entityType?: EntityType): void {
    this.showPlaygroundModal.set(true);
  }

  closePlayground(): void {
    this.showPlaygroundModal.set(false);
  }
}
