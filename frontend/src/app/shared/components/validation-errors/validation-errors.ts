import { Component, Input } from '@angular/core';
import { ToastComponent } from '../../components/toast/toast';
import { Error } from '../../../shared/domain/ui/toast';


@Component({
  selector: 'app-validation-errors',
  imports: [ToastComponent],
  templateUrl: "./validation-errors.html",
})
export default class ValidationErrors {
  @Input({required: true}) field!: any;
  protected readonly error = Error;
}