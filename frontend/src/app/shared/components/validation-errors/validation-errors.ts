import { Component, Input } from '@angular/core';
import { ToastComponent } from '../../components/toast/toast';
import { Error } from '../../../shared/domain/ui/toast';


@Component({
  selector: 'app-validation-errors',
  imports: [ToastComponent],
  templateUrl: "./validation-errors.html",
})
export class ValidationErrors {
  protected readonly error = Error;
  @Input({ required: true }) field!: any;
}