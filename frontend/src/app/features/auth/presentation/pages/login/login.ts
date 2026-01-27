import { Component, inject, ChangeDetectionStrategy, signal } from '@angular/core';
import { RouterLink } from "@angular/router";
import { LoginRequest } from '../../domain/login.request';
import { ValidationErrors } from '../../../../../shared/components/validation-errors/validation-errors';
import { EditTextComponent } from '../../../../../shared/components/edit-text/edit-text';
import { AuthenticationFacade } from '../../../services/authentication.facade.service';
import { validate } from '../../validators/login.validator';


@Component({
  selector: 'app-login',
  imports: [RouterLink, EditTextComponent, ValidationErrors],
  templateUrl: './login.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class Login {
  private readonly authFacade = inject(AuthenticationFacade);

  protected data = signal<LoginRequest>({ email: '', password: '' });
  protected readonly validators = validate(this.data);
  
  protected onSubmit() {
    this.authFacade.login(this.data()).subscribe();
  }
}