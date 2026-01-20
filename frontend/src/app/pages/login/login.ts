import { Component, inject, LOCALE_ID, signal } from '@angular/core';
import { form, required, email } from '@angular/forms/signals';
import { RouterLink } from "@angular/router";
import { LoginRequest } from '../../domain/authentication/login-request';
import { Alert } from '../../components/alert/alert';
import { Message, error, success } from '../../domain/ui/message';
import { InputComponent } from '../../components/input/input-component'
import { LoginService } from '../../services/login-service';


@Component({
  selector: 'app-login',
  imports: [RouterLink, InputComponent, Alert],
  templateUrl: './login.html',
})
export class Login {
  
  private readonly loginService = inject(LoginService);
  protected data = signal<LoginRequest>({ email: '', password: '' });

  protected readonly feedback = this.loginService.feedback

  protected loginForm = form(this.data, (schemaPath) => {
    required(schemaPath.email, { message: "O email é obrigatorio" });
    email(schemaPath.email, { message: "Formato de email invalido" });
    required(schemaPath.password, { message: "A senha é obrigatoria" });
  });

   protected formError(message: string): Message {
    return error(message);
  };

  protected onSubmit() {
     this.loginService.login(this.data());
  }
}