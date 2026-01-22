import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { AuthenticationFacade } from '../authentication.facade.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthenticationFacade);
  return authService.isAuth()
}