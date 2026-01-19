import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class HttpService {
  private http = inject(HttpClient);
  private url: string = "http://localhost:8080"

  get<T>(endpoint: string): Observable<T> {
    return this.http.get<T>(`${this.url}${endpoint}`)
  }

  post<T>(endpoint: string, body: any): Observable<T> {
    return this.http.post<T>(`${this.url}${endpoint}`, body)
  }

    put<T>(endpoint: string, body: any): Observable<T> {
    return this.http.put<T>(`${this.url}${endpoint}`, body);
  }
}