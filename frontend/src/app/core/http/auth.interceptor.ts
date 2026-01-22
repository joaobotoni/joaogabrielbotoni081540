import { inject } from '@angular/core';
import { HttpHandlerFn, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthenticationFacade } from '../../features/auth/presentation/authentication.facade.service';

export function authInterceptor(request: HttpRequest<unknown>, next: HttpHandlerFn) {
  const auth = inject(AuthenticationFacade);
  
  const bypassUrls = ['/login', '/register', '/logout', '/refresh'];
  const shouldBypass = bypassUrls.some(path => request.url.includes(path));

  if (shouldBypass) {
    return next(request);
  }

  const securedRequest = request.clone({
    setHeaders: { Authorization: `Bearer ${auth.token()}` },
    withCredentials: true
  });

  return next(securedRequest).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        return auth.refresh().pipe(
          switchMap(result => next(request.clone({
            setHeaders: { Authorization: `Bearer ${result.token}` },
            withCredentials: true
          }))),
          catchError(refreshError => {
            auth.clearSessionAndNavigate('/login');
            return throwError(() => refreshError);
          })
        );
      }
      return throwError(() => error);
    })
  );
}