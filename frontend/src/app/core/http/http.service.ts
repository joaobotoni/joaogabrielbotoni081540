import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { HttpOptions } from './http.options';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CoreHttpService {

  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080';

  public get<T>(endpoint: string, options?: HttpOptions): Observable<HttpResponse<T>> {
    return this.http.get<T>(this.buildUrl(endpoint), this.buildOptions(options)) as Observable<HttpResponse<T>>;
  }

  public post<T>(endpoint: string, body?: any, options?: HttpOptions): Observable<HttpResponse<T>> {
    return this.http.post<T>(this.buildUrl(endpoint), body, this.buildOptions(options)) as Observable<HttpResponse<T>>;
  }

  public put<T>(endpoint: string, body?: any, options?: HttpOptions): Observable<HttpResponse<T>> {
    return this.http.put<T>(this.buildUrl(endpoint), body, this.buildOptions(options)) as Observable<HttpResponse<T>>;
  }

  public delete<T>(endpoint: string, options?: HttpOptions): Observable<HttpResponse<T>> {
    return this.http.delete<T>(this.buildUrl(endpoint), this.buildOptions(options)) as Observable<HttpResponse<T>>;
  }

  public patch<T>(endpoint: string, body?: any, options?: HttpOptions): Observable<HttpResponse<T>> {
    return this.http.patch<T>(this.buildUrl(endpoint), body, this.buildOptions(options)) as Observable<HttpResponse<T>>;
  }

  private buildUrl(endpoint: string): string {
    return `${this.baseUrl}${endpoint}`;
  }

  private buildOptions(options?: HttpOptions): any {
    return { ...options, observe: 'response' };
  }
}