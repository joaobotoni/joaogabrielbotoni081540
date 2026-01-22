// http.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpOptions } from './http.options';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CoreHttpService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080';

  public get<T>(endpoint: string, options?: HttpOptions): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${endpoint}`, options);
  }

  public post<T>(endpoint: string, body: any, options?: HttpOptions): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${endpoint}`, body, options);
  }

  public put<T>(endpoint: string, body: any, options?: HttpOptions): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${endpoint}`, body, options);
  }

  public delete<T>(endpoint: string, options?: HttpOptions): Observable<T> {
    return this.http.delete<T>(`${this.baseUrl}${endpoint}`, options);
  }
}