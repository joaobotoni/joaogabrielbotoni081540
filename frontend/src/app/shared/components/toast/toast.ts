import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

import { Toast } from '../../../shared/domain/ui/toast';

@Component({
     selector: 'app-toast',
     imports: [CommonModule],
     templateUrl: "./toast.html",
})
export class ToastComponent {
     @Input({ required: true }) toast!: Toast
}