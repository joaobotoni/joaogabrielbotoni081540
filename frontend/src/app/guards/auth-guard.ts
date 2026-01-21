import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Authentication } from '../services/authentication/authentication';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(Authentication);
  return authService.isAuth()  
}