import { inject } from '@angular/core';
import { HttpHandlerFn, HttpRequest, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { catchError, switchMap, throwError, tap } from 'rxjs';
import { AuthenticationFacade } from '../../features/auth/presentation/authentication.facade.service';
import { TokenStorageService } from '../../features/auth/services/token.storage.service';

export function authInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn) {
}