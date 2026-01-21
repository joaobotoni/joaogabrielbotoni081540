import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Toast } from '../../domain/ui/toast';

@Component({
  selector: 'app-toast-component',
  imports: [CommonModule],
  templateUrl: "./toast-component.html",
})
export class ToastComponent {
  @Input({required: true}) toast!: Toast
}