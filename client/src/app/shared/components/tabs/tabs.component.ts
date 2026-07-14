import { Component, Input, Output, EventEmitter, signal, computed, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent } from '@ng-icons/core';

export interface TabItem {
  id: string;
  label: string;
  icon?: string;
  disabled?: boolean;
}

@Component({
  selector: 'app-tabs',
  standalone: true,
  imports: [CommonModule, NgIconComponent],
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.scss']
})
export class TabsComponent {
  @Input() tabs: TabItem[] = [];
  @Input() set activeTabId(value: string) {
    this._activeTabId.set(value);
  }
  @Output() tabChange = new EventEmitter<string>();

  private readonly _activeTabId: WritableSignal<string> = signal('');

  isActiveTab = computed(() => (tabId: string) => this._activeTabId() === tabId);

  switchTab(tabId: string): void {
    const tab = this.tabs.find(t => t.id === tabId);
    if (tab?.disabled) {
      return;
    }

    if (this._activeTabId() !== tabId) {
      this._activeTabId.set(tabId);
      this.tabChange.emit(tabId);
    }
  }

  isActive(tabId: string): boolean {
    return this._activeTabId() === tabId;
  }

  isDisabled(tab: TabItem): boolean {
    return tab.disabled ?? false;
  }

  getTabClasses(tab: TabItem): Record<string, boolean> {
    return {
      'tab-btn': true,
      'active': this.isActive(tab.id),
      'disabled': this.isDisabled(tab)
    };
  }

  onTabClick(tab: TabItem): void {
    if (!this.isDisabled(tab)) {
      this.switchTab(tab.id);
    }
  }

  trackByTabId(index: number, tab: TabItem): string {
    return tab.id;
  }
}
