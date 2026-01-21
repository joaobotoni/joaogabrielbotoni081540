import { Router, RouterLink } from "@angular/router";
import { Component, inject, signal } from '@angular/core';;
import { RegisterRequest } from '../../domain/authentication/register-request';
import { EditTextComponent } from '../../components/input/edit-text-component'
import { ToastValidationErrorsComponent } from '../../components/validators/toast-validation-errors-component';
import { ToastComponent } from '../../components/toast/toast-component';
import { validate } from '../../validators/register-validators';
import { Authentication } from "../../services/authentication/authentication";



@Component({
  selector: 'app-register',
  imports: [RouterLink, EditTextComponent, ToastValidationErrorsComponent, ToastComponent],
  templateUrl: './register.html'
})
export default class Register {

  username = signal('')
  private readonly auth = inject(Authentication);
  private readonly router = inject(Router)

  protected readonly data = signal<RegisterRequest>({ username: '', email: '', password: '' });
  protected readonly feedback = this.auth.toast;
  protected readonly validators = validate(this.data);

  protected onSubmit() {
    this.auth.register(this.data()).subscribe({
      next: (response) => this.router.navigate(['home', { username: response.username, email: response.email }])
    });
  }
}