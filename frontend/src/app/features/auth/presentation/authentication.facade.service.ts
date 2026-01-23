import { inject, Injectable, signal, computed } from '@angular/core';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthenticationApiService } from '../services/authentication.api.service';
import { LoginRequest } from './domain/login.request';
import { AuthenticationResponse } from './domain/authentication.response';
import { RegisterRequest } from './domain/register.request';
import { Success, Error, Toast } from '../../../shared/domain/ui/toast';
import { email } from '@angular/forms/signals';


@Injectable({ providedIn: 'root' })
export class AuthenticationFacade {

    private token: string | null = null;
    private user: AuthenticationResponse | null = null;
    private toastCallback: ((toast: Toast | null) => void) | null = null;

    constructor(
        private readonly router: Router,
        private readonly authService: AuthenticationApiService
    ) {}

    public login(request: LoginRequest): Observable<HttpResponse<AuthenticationResponse>> {
        return this.authService.login(request, { observe: 'response' }).pipe(
            tap(response => this.handleAuthResponse(response, 'Login realizado com sucesso')),
            catchError(error => this.handleError(error, 'Falha no login'))
        );
    }

    public register(request: RegisterRequest): Observable<HttpResponse<AuthenticationResponse>> {
        return this.authService.register(request, { observe: 'response' }).pipe(
            tap(response => this.handleAuthResponse(response, 'Cadastro realizado com sucesso')),
            catchError(error => this.handleError(error, 'Falha no cadastro'))
        );
    }

    public logout(): Observable<HttpResponse<void>> {
        return this.authService.logout().pipe(
            tap(() => this.handleLogout()),
            catchError(error => this.handleLogoutError(error))
        );
    }

    public refresh(): Observable<HttpResponse<void>> {
        return this.authService.refresh({ observe: 'response' }).pipe(
            tap(response => this.setToken(response)),
            catchError(error => this.handleRefreshError(error))
        );
    }

    public getToken(): string | null {
        return this.token;
    }

    public isAuthenticated(): boolean {
        return !!this.token;
    }

    public getUser(): AuthenticationResponse | null {
        return this.user;
    }

    public onToast(callback: (toast: Toast | null) => void): void {
        this.toastCallback = callback;
    }

    private handleAuthResponse(response: HttpResponse<AuthenticationResponse>, message: string): void {
        if (!response.body) return;
        
        this.setToken(response);
        this.user = response.body;
        this.showToast(Success(message));
        this.router.navigate(['/home', {username: this.user.username, email: this.user.email}]);
    }

    private handleLogout(): void {
        this.clearState();
        this.showToast(Success('Logout realizado com sucesso'));
        this.router.navigate(['/login']);
    }

    private handleError(error: HttpErrorResponse, message: string): Observable<never> {
        this.showToast(Error(error.error?.details || message));
        return throwError(() => error);
    }

    private handleLogoutError(error: HttpErrorResponse): Observable<never> {
        this.clearState();
        return throwError(() => error);
    }

    private handleRefreshError(error: HttpErrorResponse): Observable<never> {
        this.clearState();
        this.router.navigate(['/login']);
        return throwError(() => error);
    }

    private setToken(response: HttpResponse<any>): void {
        const header = response.headers.get('Authorization');
        if (header?.startsWith('Bearer ')) {
            this.token = header.substring(7);
        }
    }

    private clearState(): void {
        this.token = null;
        this.user = null;
    }

    private showToast(toast: Toast): void {
        this.toastCallback?.(toast);
        setTimeout(() => this.toastCallback?.(null), 5000);
    }
}