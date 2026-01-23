import { inject } from '@angular/core';
import { HttpHandlerFn, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthenticationFacade } from '../../features/auth/presentation/authentication.facade.service';

export function authInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn) {
  const authFacade = inject(AuthenticationFacade);
  const token = authFacade.getToken();
  
  const authReq = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !req.url.includes('/refresh')) {
        return authFacade.refresh().pipe(
          switchMap(() => next(req.clone({ setHeaders: { Authorization: `Bearer ${authFacade.getToken()}` } }))),
          catchError(err => throwError(() => err))
        );
      }
      return throwError(() => error);
    })
  );
}