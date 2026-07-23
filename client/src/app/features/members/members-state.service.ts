import { Injectable, signal } from '@angular/core';
import { ViewMode, SortOption } from './members.component';

export interface MembersState {
  viewMode: ViewMode;
  searchQuery: string;
  selectedDepartmentFilter: string;
  selectedRoleFilter: string;
  sortBy: SortOption;
}

@Injectable({
  providedIn: 'root'
})
export class MembersStateService {
  private readonly state = signal<MembersState>({
    viewMode: 'grid',
    searchQuery: '',
    selectedDepartmentFilter: '',
    selectedRoleFilter: '',
    sortBy: 'name-asc'
  });

  getState() {
    return this.state.asReadonly();
  }

  setViewMode(mode: ViewMode): void {
    this.state.update(s => ({ ...s, viewMode: mode }));
  }

  setSearchQuery(query: string): void {
    this.state.update(s => ({ ...s, searchQuery: query }));
  }

  setDepartmentFilter(departmentId: string): void {
    this.state.update(s => ({ ...s, selectedDepartmentFilter: departmentId }));
  }

  setRoleFilter(role: string): void {
    this.state.update(s => ({ ...s, selectedRoleFilter: role }));
  }

  setSortBy(sort: SortOption): void {
    this.state.update(s => ({ ...s, sortBy: sort }));
  }

  resetState(): void {
    this.state.set({
      viewMode: 'grid',
      searchQuery: '',
      selectedDepartmentFilter: '',
      selectedRoleFilter: '',
      sortBy: 'name-asc'
    });
  }
}
