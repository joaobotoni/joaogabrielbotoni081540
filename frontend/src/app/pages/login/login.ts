import { Component, inject, LOCALE_ID, signal } from '@angular/core';
import { RouterLink } from "@angular/router";
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
export class Login {

  private readonly auth = inject(Authentication);
  protected data = signal<LoginRequest>({ email: '', password: '' });
  protected readonly feedback = this.auth.feedback
  protected readonly validators = validate(this.data);

  protected onSubmit() {
    this.auth.login(this.data());
  }
}