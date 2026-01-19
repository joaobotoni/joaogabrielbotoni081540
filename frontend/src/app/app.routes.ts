import { Routes } from '@angular/router';
import { Register } from './pages/register/register';
import { Login } from './pages/login/login';
import { Home } from './pages/home/home';

export const routes: Routes = [
    {
        path: "",
        component: Register
    },
    {
        path: "login",
        component: Login
    },
      {
        path: "home",
        component: Home
    }
];
