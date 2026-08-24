import { Injectable, signal } from '@angular/core';
import { ViewMode, SortOption } from './members.component';

export interface MembersState {
  viewMode: ViewMode;
  searchQuery: string;
  selectedDepartmentFilter: string;
  selectedRoleFilter: string;
  sortBy: SortOption;
  currentPage: number;
  pageSize: number;
}

@Injectable({
  providedIn: 'root'
})
export class MembersStateService {
  private readonly state = signal<MembersState>({
    viewMode: 'list',
    searchQuery: '',
    selectedDepartmentFilter: '',
    selectedRoleFilter: '',
    sortBy: 'name-asc',
    currentPage: 1,
    pageSize: 12
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

  setCurrentPage(page: number): void {
    this.state.update(s => ({ ...s, currentPage: page }));
  }

  setPageSize(pageSize: number): void {
    this.state.update(s => ({ ...s, pageSize }));
  }

  resetState(): void {
    this.state.set({
      viewMode: 'grid',
      searchQuery: '',
      selectedDepartmentFilter: '',
      selectedRoleFilter: '',
      sortBy: 'name-asc',
      currentPage: 1,
      pageSize: 12
    });
  }
}

