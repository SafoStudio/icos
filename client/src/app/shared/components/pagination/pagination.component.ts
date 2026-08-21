import { Component, Input, Output, EventEmitter, computed, signal, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgIconComponent } from '@ng-icons/core';

export type PageItem = number | '...';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIconComponent],
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnChanges {
  @Input() currentPage: number = 1;
  @Input() totalItems: number = 0;
  @Input() pageSize: number = 10;
  @Input() pageSizeOptions: number[] = [6, 12, 24, 48];
  @Input() showPageSizeSelector: boolean = true;
  @Input() showTotalInfo: boolean = true;
  @Input() maxVisiblePages: number = 5;

  @Output() pageChange = new EventEmitter<number>();
  @Output() pageSizeChange = new EventEmitter<number>();

  private readonly _currentPage = signal(1);
  private readonly _totalItems = signal(0);
  private readonly _pageSize = signal(10);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['currentPage']) {
      this._currentPage.set(this.currentPage);
    }
    if (changes['totalItems']) {
      this._totalItems.set(this.totalItems);
    }
    if (changes['pageSize']) {
      this._pageSize.set(this.pageSize);
    }
  }

  readonly totalPages = computed(() => {
    const total = this._totalItems();
    const size = this._pageSize();
    return Math.max(1, Math.ceil(total / Math.max(1, size)));
  });

  readonly startIndex = computed(() => {
    const total = this._totalItems();
    if (total === 0) return 0;
    return (this._currentPage() - 1) * this._pageSize() + 1;
  });

  readonly endIndex = computed(() => {
    const total = this._totalItems();
    return Math.min(this._currentPage() * this._pageSize(), total);
  });

  readonly pages = computed<PageItem[]>(() => {
    const total = this.totalPages();
    const current = this._currentPage();
    const maxVisible = this.maxVisiblePages;

    if (total <= maxVisible + 2) {
      return Array.from({ length: total }, (_, i) => i + 1);
    }

    const items: PageItem[] = [];
    const half = Math.floor((maxVisible - 2) / 2);
    let start = Math.max(2, current - half);
    let end = Math.min(total - 1, current + half);

    if (current <= half + 2) {
      start = 2;
      end = maxVisible - 1;
    } else if (current >= total - half - 1) {
      start = total - maxVisible + 2;
      end = total - 1;
    }

    items.push(1);
    if (start > 2) {
      items.push('...');
    }

    for (let i = start; i <= end; i++) {
      items.push(i);
    }

    if (end < total - 1) {
      items.push('...');
    }
    items.push(total);

    return items;
  });

  goToPage(page: number): void {
    const total = this.totalPages();
    if (page < 1 || page > total || page === this.currentPage) {
      return;
    }
    this.pageChange.emit(page);
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.goToPage(this.currentPage - 1);
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages()) {
      this.goToPage(this.currentPage + 1);
    }
  }

  onPageSizeSelect(size: number | string): void {
    const numericSize = Number(size);
    if (numericSize > 0 && numericSize !== this.pageSize) {
      this.pageSizeChange.emit(numericSize);
    }
  }

  isEllipsis(item: PageItem): boolean {
    return item === '...';
  }

  asNumber(item: PageItem): number {
    return item as number;
  }
}
