import { inject, Injectable, signal } from '@angular/core';
import { Authentication } from './authentication';
import { Message, error, success } from '../domain/ui/message';
import { LoginRequest } from '../domain/authentication/login-request';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private readonly auth = inject(Authentication);

  readonly feedback = signal<Message | null>(null);

  login(data: LoginRequest) {
    this.auth.login(data).subscribe({
      next: () => {
        this.feedback.set(success("Registro realizado com sucesso!"));

      },
      error: (err) => {
        this.feedback.set(error(err.error?.detail || 'Erro ao fazer login'));
      }
    })
  };
}
