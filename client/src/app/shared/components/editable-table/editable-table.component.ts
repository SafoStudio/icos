import { Component, Input, Output, EventEmitter, signal, WritableSignal, HostListener, ElementRef, ViewChild, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';
import { ToastService } from '@services';

export interface TableColumn {
  key: string;
  label: string;
  type?: 'text' | 'number' | 'date';
  editable?: boolean;
  required?: boolean;
}

interface ValidationRule {
  validate: (value: string) => boolean;
  getMessage: (label: string) => string;
}

@Component({
  selector: 'app-editable-table',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './editable-table.component.html',
  styleUrls: ['./editable-table.component.scss']
})
export class EditableTableComponent implements OnInit {
  @Input() title: string = '';
  @Input() columns: TableColumn[] = [];
  @Input() data: any[] = [];
  @Input() loading: boolean = false;
  @Input() readOnly: boolean = false;
  @Output() addItem = new EventEmitter<any>();
  @Output() updateItem = new EventEmitter<{ index: number; item: any }>();
  @Output() deleteItem = new EventEmitter<number>();
  @Output() rowClick = new EventEmitter<any>();

  @ViewChild('menuContainer') menuContainer!: ElementRef;

  private readonly toastService = inject(ToastService);
  private idCounter = 0;

  readonly editingIndex: WritableSignal<number | null> = signal(null);
  readonly originalData: WritableSignal<any> = signal(null);
  readonly menuOpenIndex: WritableSignal<number | null> = signal(null);
  readonly isNewRow: WritableSignal<boolean> = signal(false);

  private readonly validationRules: Map<string, ValidationRule[]> = new Map();

  ngOnInit(): void {
    this.buildValidationRules();
  }

  isEditing(index: number): boolean {
    return this.editingIndex() === index;
  }

  isMenuOpen(index: number): boolean {
    return this.menuOpenIndex() === index;
  }

  isEmpty(): boolean {
    return this.data.length === 0;
  }

  onRowClick(item: any, event: MouseEvent): void {
      if (this.editingIndex() !== null) {
        return;
      }
      this.rowClick.emit(item);
    }

  addRow(): void {
    this.closeAllMenus();
    this.cancelEdit();

    const newItem = this.createEmptyItem();
    this.data.unshift(newItem);

    this.editingIndex.set(0);
    this.originalData.set({ ...newItem });
    this.isNewRow.set(true);
  }

  startEdit(index: number): void {
    this.closeAllMenus();
    this.cancelEdit();

    this.editingIndex.set(index);
    this.originalData.set({ ...this.data[index] });
    this.isNewRow.set(false);
  }

  saveRow(index: number): void {
    if (this.editingIndex() === null) return;

    const item = this.data[index];

    if (!this.isRowValid(item)) return;

    this.saveItem(item, index);
    this.cancelEdit();
  }

  cancelEdit(): void {
    const index = this.editingIndex();

    if (index !== null) {
      this.restoreOriginalData(index);
    }

    this.resetEditState();
  }

  deleteRow(index: number): void {
    if (this.isConfirmed('Are you sure you want to delete this item?')) {
      this.deleteItem.emit(index);
    }
    this.menuOpenIndex.set(null);
  }

  toggleMenu(index: number, event: Event): void {
    event.stopPropagation();

    if (this.menuOpenIndex() === index) {
      this.menuOpenIndex.set(null);
      return;
    }

    this.cancelEdit();
    this.menuOpenIndex.set(index);
  }

  onKeydown(event: KeyboardEvent, index: number): void {
    if (event.key === 'Enter') {
      this.saveRow(index);
    } else if (event.key === 'Escape') {
      this.cancelEdit();
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.action-menu-container')) {
      this.menuOpenIndex.set(null);
    }
  }

  private buildValidationRules(): void {
    this.validationRules.clear();

    this.columns.forEach(col => {
      const rules: ValidationRule[] = [];

      if (col.key === 'name') {
        rules.push({
          validate: (v: string) => v.length >= 2 && v.length <= 50,
          getMessage: (label: string) => `"${label}" must be between 2 and 50 characters.`
        });
      }

      if (col.key === 'description') {
        rules.push({
          validate: (v: string) => v.length <= 200,
          getMessage: (label: string) => `"${label}" cannot exceed 200 characters.`
        });
      }

      if (rules.length > 0) {
        this.validationRules.set(col.key, rules);
      }
    });
  }

  private createEmptyItem(): any {
    const newItem: any = { _tempId: --this.idCounter };

    this.columns.forEach(col => {
      if (col.editable !== false) {
        newItem[col.key] = '';
      }
    });

    return newItem;
  }

  private isRowValid(item: any): boolean {
    for (const col of this.columns) {
      this.trimFieldIfString(item, col);

      if (!this.isFieldValid(item, col)) {
        return false;
      }
    }
    return true;
  }

  private trimFieldIfString(item: any, col: TableColumn): void {
    const value = item[col.key];
    if (typeof value === 'string') {
      item[col.key] = value.trim();
    }
  }

  private isFieldValid(item: any, col: TableColumn): boolean {
    const value = this.getFieldValue(item, col);

    // Check required
    if (col.required && value === '') {
      this.showValidationError(`Field "${col.label}" is required.`);
      return false;
    }

    // Skip additional validation for empty values
    if (value === '') return true;

    // Apply specific validation rules
    return this.applyValidationRules(value, col);
  }

  private getFieldValue(item: any, col: TableColumn): string {
    const val = item[col.key];
    return val ? val.toString().trim() : '';
  }

  private applyValidationRules(value: string, col: TableColumn): boolean {
    const rules = this.validationRules.get(col.key);
    if (!rules) return true;

    for (const rule of rules) {
      if (!rule.validate(value)) {
        this.showValidationError(rule.getMessage(col.label));
        return false;
      }
    }

    return true;
  }

  private showValidationError(message: string): void {
    this.toastService.warning(message, 'Validation Error');
  }

  private saveItem(item: any, index: number): void {
    if (this.isNewRow()) {
      this.addItem.emit(item);
    } else {
      this.updateItem.emit({ index, item });
    }
  }

  private restoreOriginalData(index: number): void {
    if (this.isNewRow()) {
      this.data.splice(index, 1);
    } else if (this.originalData()) {
      this.data[index] = this.originalData();
    }
  }

  private resetEditState(): void {
    this.editingIndex.set(null);
    this.originalData.set(null);
    this.isNewRow.set(false);
    this.menuOpenIndex.set(null);
  }

  private closeAllMenus(): void {
    this.menuOpenIndex.set(null);
  }

  private isConfirmed(message: string): boolean {
    return confirm(message);
  }
}
