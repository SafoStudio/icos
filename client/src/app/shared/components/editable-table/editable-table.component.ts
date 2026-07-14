import { Component, Input, Output, EventEmitter, signal, WritableSignal, HostListener, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface TableColumn {
  key: string;
  label: string;
  type?: 'text' | 'number' | 'date';
  editable?: boolean;
  required?: boolean;
}

@Component({
  selector: 'app-editable-table',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './editable-table.component.html',
  styleUrls: ['./editable-table.component.scss']
})
export class EditableTableComponent {
  @Input() title: string = '';
  @Input() columns: TableColumn[] = [];
  @Input() data: any[] = [];
  @Input() loading: boolean = false;
  @Output() addItem = new EventEmitter<any>();
  @Output() updateItem = new EventEmitter<{ index: number; item: any }>();
  @Output() deleteItem = new EventEmitter<number>();

  @ViewChild('menuContainer') menuContainer!: ElementRef;

  readonly editingIndex: WritableSignal<number | null> = signal(null);
  readonly originalData: WritableSignal<any> = signal(null);
  readonly menuOpenIndex: WritableSignal<number | null> = signal(null);

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.action-menu-container')) {
      this.menuOpenIndex.set(null);
    }
  }

  toggleMenu(index: number, event: Event): void {
    event.stopPropagation();
    this.menuOpenIndex.set(this.menuOpenIndex() === index ? null : index);
  }

  startEdit(index: number): void {
    this.editingIndex.set(index);
    this.originalData.set({ ...this.data[index] });
    this.menuOpenIndex.set(null);
  }

  saveRow(index: number): void {
    if (this.editingIndex() !== null) {
      const item = this.data[index];
      const hasEmptyRequired = this.columns
        .filter(col => col.required)
        .some(col => !item[col.key] || item[col.key].toString().trim() === '');

      if (hasEmptyRequired) {
        alert('Please fill in all required fields');
        return;
      }

      this.updateItem.emit({ index, item });
      this.cancelEdit();
    }
  }

  cancelEdit(): void {
    if (this.editingIndex() !== null && this.originalData()) {
      this.data[this.editingIndex()!] = this.originalData();
    }
    this.editingIndex.set(null);
    this.originalData.set(null);
    this.menuOpenIndex.set(null);
  }

  addRow(): void {
    const newItem: any = {};
    this.columns.forEach(col => {
      if (col.editable !== false) {
        newItem[col.key] = '';
      }
    });
    this.data.unshift(newItem);
    this.editingIndex.set(0);
    this.originalData.set({ ...newItem });
    this.menuOpenIndex.set(null);
  }

  deleteRow(index: number): void {
    if (confirm('Are you sure you want to delete this item?')) {
      this.deleteItem.emit(index);
    }
    this.menuOpenIndex.set(null);
  }

  onKeydown(event: KeyboardEvent, index: number): void {
    if (event.key === 'Enter') {
      this.saveRow(index);
    } else if (event.key === 'Escape') {
      this.cancelEdit();
    }
  }

  isEditing(index: number): boolean {
    return this.editingIndex() === index;
  }

  isEmpty(): boolean {
    return this.data.length === 0;
  }

  isMenuOpen(index: number): boolean {
    return this.menuOpenIndex() === index;
  }
}
