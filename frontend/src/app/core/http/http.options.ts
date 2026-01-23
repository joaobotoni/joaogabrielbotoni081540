import { HttpContext, HttpHeaders, HttpParams } from '@angular/common/http';

export interface HttpOptions {
  headers?: HttpHeaders | { [header: string]: string | string[] };
  params?: HttpParams | { [param: string]: string | string[] };
  observe?: any
  context?: HttpContext;
  withCredentials?: boolean;
}