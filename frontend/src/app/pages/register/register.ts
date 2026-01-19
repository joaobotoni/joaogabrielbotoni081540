import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from "@angular/router";
import { Authentication } from '../../services/authentication';
import { RegisterRequest } from '../../domain/authentication/register-request';
import { MatSnackBar } from '@angular/material/snack-bar';


@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html',
})
export class Register {

  private readonly auth: Authentication = inject(Authentication);
  private snackBar = inject(MatSnackBar);
  
  content: RegisterRequest = {
    username: '',
    email: '',
    password: ''
  }

  register() {
    this.auth.register(this.content).subscribe((response) => {
      this.snackBar.open(`Bem vindo: ${response.username}`, "OK")
    }
    )
  }
}

