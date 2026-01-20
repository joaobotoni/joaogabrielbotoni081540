import { Component, inject, signal } from '@angular/core';
import { form, required, email, minLength, pattern } from '@angular/forms/signals';
import { RouterLink } from "@angular/router";
import { RegisterRequest } from '../../domain/authentication/register-request';
import { Alert } from '../../components/alert/alert';
import { InputComponent } from '../../components/input/input-component'
import { RegisterService } from '../../services/register-service';
import { Message, error } from '../../domain/ui/message';


@Component({
  selector: 'app-register',
  imports: [RouterLink, InputComponent, Alert],
  providers: [RegisterService],
  templateUrl: './register.html',
})
export class Register {
  private readonly registerService = inject(RegisterService);

  protected readonly data = signal<RegisterRequest>({
    username: '',
    email: '',
    password: ''
  });
  
  protected readonly feedback = this.registerService.feedback;

  protected readonly registerForm = form(this.data, (schemaPath) => {
    required(schemaPath.username, { message: "O nome do usuário é obrigatório" });
    required(schemaPath.email, { message: "O email é obrigatório" });
    email(schemaPath.email, { message: "Formato de email inválido" });
    required(schemaPath.password, { message: "A senha é obrigatória" });
    minLength(schemaPath.password, 8, { message: "A senha precisa ter mais de 8 dígitos" });
    pattern(schemaPath.password, /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/, {
      message: "A senha deve conter letras maiúsculas, minúsculas e números"
    });
  });

  protected formError(message: string): Message {
    return error(message);
  };


  protected onSubmit() {
    this.registerService.register(this.data());
  }
}