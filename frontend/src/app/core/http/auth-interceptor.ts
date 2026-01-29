import { inject } from '@angular/core';
import { HttpHandlerFn, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthenticationFacade } from '../../features/auth/services/authentication-facade-service';
import { Router } from '@angular/router';

const routes = ['/login', '/register', '/refresh', '/logout'];

export function authInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn) {

  const auth = inject(AuthenticationFacade);
  const token = auth.getToken();
  const router = inject(Router)
  const isPublic = routes.some(route => req.url.includes(route));

  if (token && !isPublic) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` }, withCredentials: true });
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (!isPublic && (error.status === 401 || error.status === 403)) {
        return auth.refresh().pipe(
          switchMap(() => {
            const newToken = auth.getToken();
            const newRequest = req.clone({setHeaders: { Authorization: `Bearer ${newToken}` }});
            return next(newRequest);
          }),
          catchError(refreshError => {
            auth.clear();
            router.navigate([routes[0]]);
            return throwError(() => refreshError);
          })
        );
      }
      return throwError(() => error);
    })
  );
}