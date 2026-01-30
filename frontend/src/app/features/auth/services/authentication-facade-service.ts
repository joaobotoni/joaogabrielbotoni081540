import { inject, Injectable } from '@angular/core';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthenticationService } from './authentication-service';
import { LoginRequest } from '../presentation/domain/login-request';
import { AuthenticationResponse } from '../presentation/domain/authentication-response';
import { RegisterRequest } from '../presentation/domain/register-request';
import { ToastService } from '../../../shared/services/toast-service';
import { CookieService } from 'ngx-cookie-service';
import { email } from '@angular/forms/signals';

const COOKIE_TOKEN = 'access';
const COOKIE_USER = 'user';

const COOKIE_EXPIRATION = 30 / 86400;
const ROUTES = { HOME: '/home', LOGIN: '/login' };

@Injectable({ providedIn: 'root' })
export class AuthenticationFacade {

    private readonly authService = inject(AuthenticationService);
    private readonly cookie = inject(CookieService);
    private readonly router = inject(Router);
    private readonly toast = inject(ToastService);

    public isAuth = (): boolean => !!this.getToken();

    public login(request: LoginRequest): Observable<HttpResponse<AuthenticationResponse>> {
        return this.authService.login(request, { observe: 'response' }).pipe(
            tap(res => this.handleAuth(res, ROUTES.HOME, 'Login realizado com sucesso')),
            catchError(err => this.onError(err))
        );
    }

    public register(request: RegisterRequest): Observable<HttpResponse<AuthenticationResponse>> {
        return this.authService.register(request, { observe: 'response' }).pipe(
            tap(res => this.handleAuth(res, ROUTES.HOME, 'Cadastro realizado com sucesso')),
            catchError(err => this.onError(err))
        );
    }

    public refresh(): Observable<HttpResponse<AuthenticationResponse>> {
        return this.authService.refresh({ observe: 'response' }).pipe(
            tap(res => this.handleAuth(res)),
            catchError(err => {
                this.clear();
                return this.onError(err);
            })
        );
    }

    public logout(): void {
        this.authService.logout().subscribe({
            next: () => this.clear(),
            error: err => this.onError(err)
        });
    }

    public getToken = (): string | null => this.cookie.get(COOKIE_TOKEN) || null;

    public getUser(): AuthenticationResponse | null {
        const data = this.cookie.get(COOKIE_USER);
        return data ? JSON.parse(data) : null;
    }

    public clear(): void {
        this.cookie.delete(COOKIE_TOKEN, '/');
        this.cookie.delete(COOKIE_USER, '/');
        this.navigate(ROUTES.LOGIN, 'Logout realizado com sucesso');
    }

    private handleAuth(res: HttpResponse<AuthenticationResponse>, route?: string, msg?: string): void {
        const data = res.body;
        if (!data?.token) return;

        this.save(COOKIE_TOKEN, data.token);
        this.save(COOKIE_USER, JSON.stringify({ username: data.username, email: data.email }));

        if (route && msg) this.navigate(route, msg);
    }

    private save(name: string, value: string): void {
        this.cookie.set(name, value, COOKIE_EXPIRATION, '/');
    }

    private navigate(route: string, msg: string): void {
        this.router.navigate([route]).then(() => this.toast.success(msg));
    }

    private onError(err: HttpErrorResponse): Observable<never> {
        this.toast.error(err.error?.detail || 'Erro na operação');
        return throwError(() => err);
    }
}