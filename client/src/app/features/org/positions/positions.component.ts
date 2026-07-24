import { Component, inject, computed, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EditableTableComponent, type TableColumn } from '@components';
import { PositionsService } from './positions.service';
import { AuthService, ToastService} from '@services';
import { type Position } from '@models';

@Component({
  selector: 'app-positions',
  standalone: true,
  imports: [CommonModule, EditableTableComponent],
  templateUrl: './positions.component.html',
  styleUrls: ['./positions.component.scss']
})
export class PositionsComponent {
  private readonly authService = inject(AuthService);
  private readonly positionsService = inject(PositionsService);
  private readonly toastService = inject(ToastService);

  readonly companyId = computed(() => this.authService.currentUser()?.companyId || null);
  readonly positions = signal<Position[]>([]);
  readonly loading = signal<boolean>(false);

  readonly columns: TableColumn[] = [
    { key: 'name', label: 'Name', required: true },
    { key: 'description', label: 'Description', required: false }
  ];

  constructor() {
    effect(() => {
      const id = this.companyId();
      if (id) {
        console.log('Company ID changed in Positions:', id);
        this.loadPositions();
      }
    });
  }

  private loadPositions(): void {
    const companyId = this.companyId();
    if (!companyId) {
      console.warn('No company ID available');
      return;
    }

    console.log('Loading positions for company:', companyId);
    this.loading.set(true);

    this.positionsService.getPositions(companyId).subscribe({
      next: (data) => {
        console.log('Positions data received:', data);
        this.positions.set(data);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading positions:', error);
        this.toastService.error('Failed to load positions.');
        this.loading.set(false);
      }
    });
  }

  onAddPosition(item: Position): void {
    const companyId = this.companyId();
    if (!companyId) {
      this.toastService.error('No company selected.');
      return;
    }

    this.loading.set(true);

    this.positionsService.createPosition(companyId, item.name, item.description || '').subscribe({
      next: (newPosition) => {
        console.log('Position created:', newPosition);
        this.positions.update(positions => [newPosition, ...positions]);
        this.toastService.success(`Position "${newPosition.name}" created successfully.`);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error creating position:', error);
        this.toastService.error('Failed to create position.');
        this.loading.set(false);
        this.loadPositions();
      }
    });
  }

  onUpdatePosition(event: { index: number; item: Position }): void {
    const { index, item } = event;
    this.loading.set(true);

    this.positionsService.updatePosition(item.id, item.name, item.description || '').subscribe({
      next: (updatedPosition) => {
        console.log('Position updated:', updatedPosition);
        this.positions.update(positions => {
          const updated = [...positions];
          updated[index] = updatedPosition;
          return updated;
        });
        this.toastService.success(`Position "${updatedPosition.name}" updated successfully.`);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error updating position:', error);
        this.toastService.error('Failed to update position.');
        this.loading.set(false);
        this.loadPositions();
      }
    });
  }

  onDeletePosition(index: number): void {
    const position = this.positions()[index];
    this.loading.set(true);

    this.positionsService.deletePosition(position.id).subscribe({
      next: () => {
        console.log('Position deleted:', position.id);
        this.positions.update(positions => positions.filter((_, i) => i !== index));
        this.toastService.success(`Position "${position.name}" deleted successfully.`);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error deleting position:', error);
        this.toastService.error('Failed to delete position. It might be in use.');
        this.loading.set(false);
      }
    });
  }
}
