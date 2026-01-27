// authentication.facade.ts
import { inject, Injectable } from '@angular/core';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthenticationService } from './authentication.service';
import { LoginRequest } from '../presentation/domain/login.request';
import { AuthenticationResponse } from '../presentation/domain/authentication.response';
import { RegisterRequest } from '../presentation/domain/register.request';
import { ToastService } from '../../../shared/services/toast.service';
import { CookieService } from 'ngx-cookie-service';

const cookieToken = 'access';
const cookieUser = 'user';
const cookieExpiration = 5 / 1440;
const prefix = 'Bearer ';
const routes = {
    home: '/home',
    login: '/login'
};

@Injectable({ providedIn: 'root' })
export class AuthenticationFacade {

    private readonly cookie = inject(CookieService);
    private readonly router = inject(Router);
    private readonly authService = inject(AuthenticationService);
    private readonly toast = inject(ToastService);

    public isAuthenticated(): boolean {
        return !!this.getToken();
    }

    public login(request: LoginRequest): Observable<HttpResponse<AuthenticationResponse>> {
        return this.authService.login(request, { observe: 'response' }).pipe(
            tap(response => this.handleAuthResponse(response, routes.home, 'Login realizado com sucesso')),
            catchError(error => this.handleError(error))
        );
    }

    public register(request: RegisterRequest): Observable<HttpResponse<AuthenticationResponse>> {
        return this.authService.register(request, { observe: 'response' }).pipe(
            tap(response => this.handleAuthResponse(response, routes.home, 'Cadastro realizado com sucesso')),
            catchError(error => this.handleError(error))
        );
    }

    public logout(): Observable<HttpResponse<void>> {
        return this.authService.logout().pipe(
            tap(() => {
                this.clearState();
                this.navigateWithToast(routes.login, 'Logout realizado com sucesso');
            }),
            catchError(error => this.handleError(error))
        );
    }

    public refresh(): Observable<HttpResponse<void>> {
        return this.authService.refresh({ observe: 'response' }).pipe(
            tap(response => this.setToken(response)),
            catchError(error => this.handleError(error))
        );
    }

    public getUser(): AuthenticationResponse | null {
        const cookie = this.cookie.get(cookieUser);
        return cookie ? JSON.parse(cookie) : null;
    }

    public getToken(): string | null {
        return this.cookie.get(cookieToken) || null;
    }

    private handleAuthResponse(response: HttpResponse<AuthenticationResponse>, route: string, message: string): void {
        if (!response.body) return;
        this.setToken(response);
        this.setUser(response);
        this.navigateWithToast(route, message);
    }

    private setToken(response: HttpResponse<any>): void {
        const header = response.headers.get('Authorization');
        const token = header?.startsWith(prefix) ? header.substring(prefix.length) : null;
        
        if (token) {
            this.setCookie(cookieToken, token);
        }
    }

    private setUser(response: HttpResponse<any>): void {
        if (response.body) {
            this.setCookie(cookieUser, JSON.stringify(response.body));
        }
    }

    private setCookie(name: string, value: string): void {
        this.cookie.set(name, value, cookieExpiration, '/');
    }

    private navigateWithToast(route: string, message: string): void {
        this.router.navigate([route]).then(() => this.toast.success(message));
    }

    private handleError(error: HttpErrorResponse): Observable<never> {
        this.toast.error(error.error?.detail || 'Erro desconhecido');
        return throwError(() => error);
    }

    private clearState(): void {
        this.cookie.delete(cookieToken, '/');
        this.cookie.delete(cookieUser, '/');
    }
}