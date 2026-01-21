import { inject, Injectable, signal } from '@angular/core';
import { catchError, finalize, Observable, tap, throwError } from 'rxjs';
import { HttpService } from '../http/http-service';
import { RegisterRequest } from '../../domain/authentication/register-request';
import { RegisterResponse } from '../../domain/authentication/register-response';
import { LoginRequest } from '../../domain/authentication/login-request';
import { LoginResponse } from '../../domain/authentication/login-response';
import { TokenResponse } from '../../domain/authentication/token-response';
import { Error, Success, Toast } from '../../domain/ui/toast';

@Injectable({ providedIn: 'root' })
export class Authentication {
  private readonly http = inject(HttpService);
  private readonly options = { withCredentials: true };

  readonly token = signal<string>('');
  readonly isAuth = signal<boolean>(false);
  readonly toast = signal<Toast | null>(null);

  register(body: RegisterRequest): Observable<RegisterResponse> {
    return this.auth<RegisterResponse>('/register', body, 'Registro realizado com sucesso!');
  }

  login(body: LoginRequest): Observable<LoginResponse> {
    return this.auth<LoginResponse>('/login', body, 'Login realizado com sucesso!');
  }

  refresh(): Observable<TokenResponse> {
    return this.http
      .put<TokenResponse>('/refresh', {}, this.options)
      .pipe(tap((res) => this.setToken(res.token)),
        catchError((err) => this.handleError(err, 'Sessão expirada. Faça login novamente.'))
      );
  }

  logout(): Observable<void> {
    return this.http
      .post<void>('/logout', {}, this.options)
      .pipe(tap(() => this.success('Logout realizado com sucesso!')),
        catchError((err) => this.handleError(err)),
        finalize(() => this.clear())
      );
  }

  private auth<T extends { token: string }>(
    endpoint: string,
    body: object,
    message: string
  ): Observable<T> {
    return this.http
      .post<T>(endpoint, body, this.options)
      .pipe(tap((res) => this.ok(res.token, message)),
        catchError((err) => this.handleError(err))
      );
  }

  private ok(token: string, message: string): void {
    this.setToken(token);
    this.success(message);
  }

  private setToken(token: string): void {
    this.token.set(token);
    this.isAuth.set(true);
  }

  private clear(): void {
    this.token.set('');
    this.isAuth.set(false);
  }

  private success(message: string): void {
    this.toast.set(Success(message));
  }

  private fail(message: string): void {
    this.toast.set(Error(message));
  }

  private readonly handleError = (err: unknown, custom?: string): Observable<never> => {
    const message = this.getMessage(err, custom);
    this.fail(message);
    if (custom) {
      this.clear();
    }
    return throwError(() => err);
  };

  private getMessage(err: unknown, custom?: string): string {
    return custom || (err as any)?.error?.detail || 'Erro inesperado. Tente novamente mais tarde.';
  }
}