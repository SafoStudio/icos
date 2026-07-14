import { Injectable, signal, WritableSignal } from '@angular/core';

export interface Toast {
  id: string;
  type: 'success' | 'error' | 'info' | 'warning';
  title?: string;
  message: string;
  duration?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  readonly toasts: WritableSignal<Toast[]> = signal([]);

  show(type: Toast['type'], message: string, title?: string, duration: number = 4000): void {
    const id = Math.random().toString(36).substring(2, 9);
    const newToast: Toast = { id, type, message, title, duration };
    
    this.toasts.update(current => [...current, newToast]);

    if (duration > 0) {
      setTimeout(() => {
        this.remove(id);
      }, duration);
    }
  }

  success(message: string, title: string = 'Success'): void {
    this.show('success', message, title);
  }

  error(message: string, title: string = 'Error'): void {
    this.show('error', message, title);
  }

  info(message: string, title: string = 'Info'): void {
    this.show('info', message, title);
  }

  warning(message: string, title: string = 'Warning'): void {
    this.show('warning', message, title);
  }

  remove(id: string): void {
    this.toasts.update(current => current.filter(t => t.id !== id));
  }
}
