import { inject, Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { HttpResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class TokenStorageService {
  private readonly cookieService = inject(CookieService)
  private key = 'access_token'

  public token(token: string): void {
    this.cookieService.set(this.key, token, {
      path: "/",
      secure: true,
      sameSite: 'Strict',
      expires: 10  
    })
  }

  public parseToken(res: HttpResponse<any>): string | null {
    const header = res.headers.get('Authorization') || res.headers.get('x-access-token');
    if (!header) return null;
    return header.startsWith('Bearer ') ? header.substring(7) : header;
  }

  public getToken(): string {
    return this.cookieService.get(this.key);
  }

  public clearToken(): void {
    this.cookieService.delete(this.key, '/');
  }

  public hasToken(): boolean {
    return this.cookieService.check(this.key)
  }
}