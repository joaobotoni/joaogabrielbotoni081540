import { inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap, catchError, throwError, finalize } from 'rxjs';
import { AuthenticationApiService } from '../services/authentication.api.service';
import { LoginRequest } from './domain/login.request';
import { AuthenticationResponse } from './domain/authentication.response';
import { RegisterRequest } from './domain/register.request';
import { TokenResponse } from './domain/token.response';
import { Error, Success, Toast } from '../../../shared/domain/ui/toast';

@Injectable({ providedIn: 'root' })
export class AuthenticationFacade {
  private readonly authApi = inject(AuthenticationApiService);
  private readonly router = inject(Router);

  readonly token = signal<string>('');
  readonly isAuth = signal<boolean>(false);
  readonly toast = signal<Toast | null>(null);

  public register(body: RegisterRequest): Observable<AuthenticationResponse> {
    return this.authApi.register(body).pipe(
      tap((res) => this.handleAuthSuccess('Registro realizado com sucesso!', 'home', { username: res.username, email: res.email })),
      catchError((err) => this.handleAuthError(err))
    );
  }

  public login(body: LoginRequest): Observable<AuthenticationResponse> {
    return this.authApi.login(body).pipe(
      tap((res) => this.handleAuthSuccess('Login realizado com sucesso!', 'home', { username: res.username, email: res.email })),
      catchError((err) => this.handleAuthError(err))
    );
  }

  public refresh(): Observable<TokenResponse> {
    return this.authApi.refresh().pipe(
      tap((res) => this.setToken(res.token)),
      catchError((err) => this.handleAuthError(err, 'Sessão expirada. Faça login novamente.'))
    );
  }

  public logout(): Observable<void> {
    return this.authApi.logout().pipe(
      tap(() => this.success('Logout realizado com sucesso!')),
      catchError((err) => this.handleAuthError(err)),
      finalize(() => this.clearSessionAndNavigate('/login'))
    );
  }

  private handleAuthSuccess(message: string, navigateTo: string, params: object): void {
    this.success(message);
    this.isAuth.set(true); 
    this.router.navigate([navigateTo, params]);
  }

  private handleAuthError(err: unknown, customMessage?: string): Observable<never> {
    const message = this.getMessage(err, customMessage);
    this.fail(message);
    if (customMessage) {
      this.clearSessionAndNavigate('/login');
    }
    return throwError(() => err);
  }

  private setToken(token: string): void {
    this.token.set(token);
    this.isAuth.set(true);
  }

  public clearSessionAndNavigate(route: string): void {
    this.token.set('');
    this.isAuth.set(false);
    this.router.navigate([route]);
  }

  private success(message: string): void {
    this.toast.set(Success(message));
  }

  private fail(message: string): void {
    this.toast.set(Error(message));
  }

  private getMessage(err: unknown, custom?: string): string {
    return custom || (err as any)?.error?.detail || 'Erro inesperado. Tente novamente mais tarde.';
  }
}