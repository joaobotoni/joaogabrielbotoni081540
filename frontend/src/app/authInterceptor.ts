import { inject } from '@angular/core';
import { HttpHandlerFn, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { catchError, switchMap, throwError } from 'rxjs';
import { Authentication } from './services/authentication/authentication';

export function authInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn) {
  const auth = inject(Authentication);
  const authToken = auth.token();

  const newReq = req.clone({
    setHeaders: authToken ? { Authorization: `Bearer ${authToken}` } : {},
    withCredentials: true
  });

  return next(newReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        return auth.refresh().pipe(
          switchMap(response => next(req.clone({
            setHeaders: { Authorization: `Bearer ${response.token}` },
            withCredentials: true
          }))),
          catchError(refreshError => {
            auth.logout();
            return throwError(() => refreshError);
          })
        );
      }
      return throwError(() => error);
    })
  );
}