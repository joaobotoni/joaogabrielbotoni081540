import { Component, inject, LOCALE_ID, signal } from '@angular/core';
import { Router, RouterLink } from "@angular/router";
import { LoginRequest } from '../../domain/authentication/login-request';
import { ToastComponent } from '../../components/toast/toast-component';
import { ToastValidationErrorsComponent } from '../../components/validators/toast-validation-errors-component';
import { EditTextComponent } from '../../components/input/edit-text-component'
import { Authentication } from "../../services/authentication/authentication";
import { validate } from '../../validators/login-validators';


@Component({
  selector: 'app-login',
  imports: [RouterLink, EditTextComponent, ToastComponent, ToastValidationErrorsComponent],
  templateUrl: './login.html',
})
export default class Login {

  private readonly auth = inject(Authentication);
  private readonly router = inject(Router)

  protected data = signal<LoginRequest>({ email: '', password: '' });
  protected readonly feedback = this.auth.toast
  protected readonly validators = validate(this.data);

  protected onSubmit() {
    this.auth.login(this.data()).subscribe({
      next: (response) => this.router.navigate(['home', { username: response.username, email: response.email }])
    })
  }
}