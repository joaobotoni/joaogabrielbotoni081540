// register.service.ts
import { inject, Injectable, signal } from '@angular/core';
import { Authentication } from './authentication';
import { Message, error, success } from '../domain/ui/message';
import { RegisterRequest } from '../domain/authentication/register-request';

@Injectable()
export class RegisterService {
  private readonly auth = inject(Authentication);
  
  readonly feedback = signal<Message | null>(null);

  register(data: RegisterRequest) {
    this.auth.register(data).subscribe({
      next: () => {
        this.feedback.set(success("Registro realizado com sucesso!"));
      },
      error: (err) => {
        this.feedback.set(error(err.error?.detail || 'Erro ao registrar'));
      }
    });
  }
}