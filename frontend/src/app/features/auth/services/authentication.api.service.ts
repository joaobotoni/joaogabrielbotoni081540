import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { CoreHttpService } from '../../../core/http/http.service';
import { RegisterRequest } from '../presentation/domain/register.request';
import { LoginRequest } from '../presentation/domain/login.request';
import { AuthenticationResponse } from '../presentation/domain/authentication.response';
import { HttpOptions } from '../../../core/http/http.options';

@Injectable({ providedIn: 'root' })
export class AuthenticationApiService {

  private readonly http = inject(CoreHttpService);

  public register(body: RegisterRequest, options?: HttpOptions): Observable<HttpResponse<AuthenticationResponse>> {
    return this.http.post<AuthenticationResponse>('/register', body, options);
  }

  public login(body: LoginRequest, options?: HttpOptions): Observable<HttpResponse<AuthenticationResponse>> {
    return this.http.post<AuthenticationResponse>('/login', body, options);
  }

  public refresh(options?: HttpOptions): Observable<HttpResponse<void>> {
    return this.http.put<void>('/refresh', options);
  }

  public logout(): Observable<HttpResponse<void>> {
    return this.http.post<void>('/logout');
  }

}