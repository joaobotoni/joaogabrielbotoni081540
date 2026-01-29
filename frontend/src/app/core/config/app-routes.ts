import { Routes } from '@angular/router';
import { authGuard } from '../../features/auth/presentation/guards/auth-guard';
import { authResolver } from '../../features/auth/presentation/resolvers/auth-resolver';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'register',
    pathMatch: 'full'
  },
  {
    path: "register",
    loadComponent: () => import('../../features/auth/presentation/pages/register/register')
  },
  {
    path: "login",
    loadComponent: () => import('../../features/auth/presentation/pages/login/login')
  },
  {
    path: "home",
    loadComponent: () => import('../../features/home/presentation/home/home'),
    resolve: { user: authResolver },
    canActivate: [authGuard]
  },
  {
    path: "**",
    redirectTo: "register"
  },
];
