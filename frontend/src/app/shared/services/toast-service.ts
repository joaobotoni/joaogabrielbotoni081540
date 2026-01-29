import { Injectable, signal } from '@angular/core';
import { Toast, Success, Error, Warning } from '../domain/ui/toast';

@Injectable({ providedIn: 'root' })
export class ToastService {
  
  toast = signal<Toast | null>(null);

  success(message: string): void {
    this.show(Success(message));
  }

  error(message: string): void {
    this.show(Error(message));
  }

  warning(message: string): void {
    this.show(Warning(message));
  }

  clear(): void {
    this.toast.set(null);
  }

  private show(toast: Toast): void {
    this.toast.set(toast);
    setTimeout(() => this.clear(), 1400);
  }
}