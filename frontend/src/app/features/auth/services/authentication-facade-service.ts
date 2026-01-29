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

const COOKIE_TOKEN = 'access';
const COOKIE_USER = 'user';
const COOKIE_EXPIRATION = 30 / 86400;
const TOKEN_PREFIX = 'Bearer ';
const ROUTES = { HOME: '/home', LOGIN: '/login' };

@Injectable({ providedIn: 'root' })
export class AuthenticationFacade {
    
    private readonly authService = inject(AuthenticationService);

    private readonly cookie = inject(CookieService);
    private readonly router = inject(Router);
    private readonly toast = inject(ToastService);

    public isAuth(): boolean {
        return !!this.getToken();
    }

    public login(request: LoginRequest): Observable<HttpResponse<AuthenticationResponse>> {
        return this.authService.login(request, { observe: 'response' }).pipe(
            tap(res => this.onAuth(res, ROUTES.HOME, 'Login realizado com sucesso')),
            catchError(err => this.onError(err))
        );
    }

    public register(request: RegisterRequest): Observable<HttpResponse<AuthenticationResponse>> {
        return this.authService.register(request, { observe: 'response' }).pipe(
            tap(res => this.onAuth(res, ROUTES.HOME, 'Cadastro realizado com sucesso')),
            catchError(err => this.onError(err))
        );
    }

    public logout(): Observable<HttpResponse<void>> {
        return this.authService.logout().pipe(
            tap(() => { this.clear(); this.navigate(ROUTES.LOGIN, 'Logout realizado com sucesso'); }),
            catchError(err => this.onError(err))
        );
    }

    public refresh(): Observable<HttpResponse<AuthenticationResponse>> {
        return this.authService.refresh({ observe: 'response' }).pipe(
            tap(res => { this.saveToken(res); this.saveUser(res) }),
            catchError(err => this.onError(err))
        );
    }

    public getUser(): AuthenticationResponse | null {
        const data = this.cookie.get(COOKIE_USER);
        return data ? JSON.parse(data) : null;
    }

    public getToken(): string | null {
        return this.cookie.get(COOKIE_TOKEN) || null;
    }

    public clear(): void {
        this.cookie.delete(COOKIE_TOKEN, '/');
        this.cookie.delete(COOKIE_USER, '/');
    }

    private onAuth(res: HttpResponse<AuthenticationResponse>, route: string, msg: string): void {
        if (!res.body) return;
        this.saveToken(res);
        this.saveUser(res);
        this.navigate(route, msg);
    }

    private saveToken(res: HttpResponse<AuthenticationResponse>): void {
        const header = res.headers.get('Authorization');
        const token = header?.startsWith(TOKEN_PREFIX) ? header.substring(TOKEN_PREFIX.length) : null;
        if (token) this.save(COOKIE_TOKEN, token);
    }

    private saveUser(res: HttpResponse<AuthenticationResponse>): void {
        if (res.body) this.save(COOKIE_USER, JSON.stringify(res.body));
    }

    private save(name: string, value: string): void {
        this.cookie.set(name, value, COOKIE_EXPIRATION, '/');
    }

    private navigate(route: string, msg: string): void {
        this.router.navigate([route]).then(() => this.toast.success(msg));
    }

    private onError(err: HttpErrorResponse): Observable<never> {
        this.toast.error(err.error?.detail || 'Erro desconhecido');
        return throwError(() => err);
    }
}