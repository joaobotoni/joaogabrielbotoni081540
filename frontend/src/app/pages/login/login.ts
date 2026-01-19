import { Component, inject } from '@angular/core';
import { RouterLink } from "@angular/router";
import { Authentication } from '../../services/authentication';
import { LoginRequest } from '../../domain/authentication/login-request';
import { FormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  imports: [RouterLink, FormsModule],
  templateUrl: './login.html',
})
export class Login {

  private readonly auth: Authentication = inject(Authentication);
  private snackBar = inject(MatSnackBar);

  content: LoginRequest = {
    email: '',
    password: ''
  }

  login() {
    this.auth.login(this.content).subscribe((response) => {
      this.snackBar.open(`Olá ${response.username}`, "OK")
    })
  }
}
