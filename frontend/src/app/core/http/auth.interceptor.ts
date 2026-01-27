import { inject } from '@angular/core';
import { HttpHandlerFn, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthenticationFacade } from '../../features/auth/services/authentication.facade.service';

const routes = ['/login', '/register', '/refresh', '/logout'];

export function authInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn) {
  const auth = inject(AuthenticationFacade);
  const token = auth.getToken();
  
  const isPublicRoute = routes.some(route => req.url.includes(route));

  if (token && !isPublicRoute) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !isPublicRoute && auth.isAuthenticated()) {
        return auth.refresh().pipe(
          switchMap(() => {
            const newToken = auth.getToken();
            const newRequest = req.clone({ 
              setHeaders: { Authorization: `Bearer ${newToken}` } 
            });
            return next(newRequest);
          }),
          catchError(err => {
            auth.logout();
            return throwError(() => err);
          })
        );
      }
      return throwError(() => error);
    })
  );
}