import { RouterLink } from "@angular/router";
import { Component, inject, signal } from '@angular/core';
import { RegisterRequest } from '../../domain/register-request';
import { EditTextComponent } from '../../../../../shared/components/edit-text/edit-text';
import { ValidationErrors } from '../../../../../shared/components/validation-errors/validation-errors';
import { validate } from '../../validators/register-validator';
import { AuthenticationFacade } from '../../../services/authentication-facade-service';


@Component({
  selector: 'app-register',
  imports: [RouterLink, EditTextComponent, ValidationErrors],
  templateUrl: './register.html'
})
export default class Register {
  protected readonly data = signal<RegisterRequest>({ username: '', email: '', password: '', });
  
  private readonly authFacade = inject(AuthenticationFacade);
  protected readonly validators = validate(this.data);

  protected onSubmit() {
    this.authFacade.register(this.data()).subscribe();
  }
}