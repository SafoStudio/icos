import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent } from '@ng-icons/core';
import { ToastService, type Toast } from '@services';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule, NgIconComponent],
  templateUrl: './toast-container.component.html',
  styleUrls: ['./toast-container.component.scss']
})
export class ToastContainerComponent {
  readonly toastService = inject(ToastService);
  readonly toasts = this.toastService.toasts;

  getIconName(type: Toast['type']): string {
    switch (type) {
      case 'success':
        return 'tablerCheck';
      case 'error':
        return 'tablerAlertCircle';
      case 'warning':
        return 'tablerAlertCircle';
      case 'info':
        return 'tablerInfoCircle';
      default:
        return 'tablerInfoCircle';
    }
  }

  remove(id: string): void {
    this.toastService.remove(id);
  }
}
