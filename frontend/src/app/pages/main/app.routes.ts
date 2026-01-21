import { Routes } from '@angular/router';
import { authGuard } from '../../guards/auth-guard'; // Ajuste o caminho

export const routes: Routes = [
    {
        path: "register",
        loadComponent: () => import('../register/register')
    },
    {
        path: "login",
        loadComponent: () => import('../login/login')
    },
    {
        path: "home",
        loadComponent: () => import('../home/home'),
        canActivate: [authGuard]
    },
    {
        path: "**",
        redirectTo: "register"
    },

];