import { inject } from '@angular/core';
import type { ResolveFn } from '@angular/router';
import type { AuthenticationResponse } from '../domain/authentication.response';
import { AuthenticationFacade } from '../../services/authentication.facade.service';

export const authResolver: ResolveFn<AuthenticationResponse | null> = () => {
    const user = inject(AuthenticationFacade)
    return user.getUser()
}