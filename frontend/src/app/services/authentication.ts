import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { RegisterRequest } from '../domain/authentication/register-request';
import { RegisterResponse } from '../domain/authentication/register-response';
import { LoginRequest } from '../domain/authentication/login-request';
import { LoginResponse } from '../domain/authentication/login-response';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Authentication {

  private http = inject(HttpClient);

  private readonly url: string = "http://localhost:8080"

  register(body: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.url}/register`, body, { withCredentials: true })
  }

  login(body: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.url}/login`, body, { withCredentials: true });
  }
}
