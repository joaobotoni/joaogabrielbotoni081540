import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CoreHttpService } from '../../../core/http/http.service';
import { RegisterRequest } from '../presentation/domain/register.request';
import { LoginRequest } from '../presentation/domain/login.request';
import { AuthenticationResponse } from '../presentation/domain/authentication.response';

import { TokenResponse } from '../presentation/domain/token.response';
import { HttpOptions } from '../../../core/http/http.options';

@Injectable({ providedIn: 'root' })
export class AuthenticationApiService {

  private readonly http = inject(CoreHttpService);
  private readonly options: HttpOptions = { withCredentials: true };

  public register(body: RegisterRequest): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>('/register', body, this.options);
  }

  public login(body: LoginRequest): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>('/login', body, this.options);
  }

  public refresh(): Observable<TokenResponse> {
    return this.http.put<TokenResponse>('/refresh', {}, this.options);
  }

  public logout(): Observable<void> {
    return this.http.post<void>('/logout', {}, this.options);
  }
}