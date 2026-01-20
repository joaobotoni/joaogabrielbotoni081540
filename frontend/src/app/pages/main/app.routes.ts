import { Routes } from '@angular/router';
import { Register } from '../register/register';
import { Login } from '../login/login';
import { Home } from '../home/home';

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
