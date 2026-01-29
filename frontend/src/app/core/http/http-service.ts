import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { HttpOptions } from './http-options';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CoreHttpService {

  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080';

  public get<T>(endpoint: string, options?: HttpOptions): Observable<HttpResponse<T>> {
    return this.http.get<T>(this.url(endpoint), this.build(options)) as Observable<HttpResponse<T>>;
  }

  public post<T>(endpoint: string, body?: any, options?: HttpOptions): Observable<HttpResponse<T>> {
    return this.http.post<T>(this.url(endpoint), body, this.build(options)) as Observable<HttpResponse<T>>;
  }

  public put<T>(endpoint: string, body?: any, options?: HttpOptions): Observable<HttpResponse<T>> {
    return this.http.put<T>(this.url(endpoint), body, this.build(options)) as Observable<HttpResponse<T>>;
  }

  public delete<T>(endpoint: string, options?: HttpOptions): Observable<HttpResponse<T>> {
    return this.http.delete<T>(this.url(endpoint), this.build(options)) as Observable<HttpResponse<T>>;
  }

  public patch<T>(endpoint: string, body?: any, options?: HttpOptions): Observable<HttpResponse<T>> {
    return this.http.patch<T>(this.url(endpoint), body, this.build(options)) as Observable<HttpResponse<T>>;
  }

  private url(endpoint: string): string {
    return `${this.baseUrl}${endpoint}`;
  }

  private build(options?: HttpOptions): any {
    return { ...options, observe: 'response', withCredentials: true };
  }
}