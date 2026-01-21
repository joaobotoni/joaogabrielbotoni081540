import { Component, Input } from '@angular/core';
import { ToastComponent } from '../toast/toast-component';
import { Error } from '../../domain/ui/toast';


@Component({
  selector: 'app-toast-validation-errors-component',
  imports: [ToastComponent],
  templateUrl: "./toast-validation-errors-component.html",
})
export class ToastValidationErrorsComponent {
  @Input({required: true}) field!: any;
  protected readonly error = Error;
}