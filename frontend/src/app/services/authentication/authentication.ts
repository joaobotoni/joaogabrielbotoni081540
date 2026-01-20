import { inject, Injectable, signal } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { RegisterRequest } from '../../domain/authentication/register-request';
import { RegisterResponse } from '../../domain/authentication/register-response';
import { LoginRequest } from '../../domain/authentication/login-request';
import { LoginResponse } from '../../domain/authentication/login-response';
import { TokenResponse } from '../../domain/authentication/token-response';
import { HttpService } from '../http/http-service';
import { Success, Error, Toast } from '../../domain/ui/toast-props';

@Injectable({ providedIn: 'root' })
export class Authentication {
  private readonly http = inject(HttpService);

  readonly token = signal('');
  readonly feedback = signal<Toast | null>(null);

  register(body: RegisterRequest): void {
    this.auth<RegisterResponse>('/register', body, 'Registro realizado com sucesso!').subscribe();
  }

  login(body: LoginRequest): void {
    this.auth<LoginResponse>('/login', body, 'Login realizado com sucesso!').subscribe();
  }

  refresh(): Observable<TokenResponse> {
    return this.http.put<TokenResponse>('/refresh', {}, { withCredentials: true }).pipe(
      tap(res => this.token.set(res.token)),
      catchError(err => this.error(err, 'Sessão expirada. Faça login novamente.'))
    );
  }

  logout(): void {
    this.http.post('/logout', {}, { withCredentials: true }).subscribe();
    this.feedback.set(Success('Logout realizado com sucesso!'));
    this.clear();
  }

  private auth<T extends TokenResponse>(path: string, body: unknown, msg: string): Observable<T> {
    return this.http.post<T>(path, body, { withCredentials: true }).pipe(
      tap(res => this.success(res.token, msg)),
      catchError(err => this.error(err))
    );
  }

  private success(token: string, msg: string): void {
    this.token.set(token);
    this.feedback.set(Success(msg));
  }

  private error(err: unknown, custom?: string): Observable<never> {
    const msg = custom || (err as any)?.error?.detail || 'Erro inesperado. Tente novamente mais tarde.';
    this.feedback.set(Error(msg));
    if (custom) this.clear();
    return throwError(() => err);
  }

  private clear(): void {
    this.token.set('');
  }
}