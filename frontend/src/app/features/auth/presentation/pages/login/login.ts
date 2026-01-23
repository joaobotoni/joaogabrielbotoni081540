import { Component, inject, ChangeDetectionStrategy, signal } from '@angular/core';
import { RouterLink } from "@angular/router";
import { LoginRequest } from '../../domain/login.request';
import { ToastComponent } from '../../../../../shared/components/toast/toast';
import ValidationErrors from '../../../../../shared/components/validation-errors/validation-errors';
import { EditTextComponent } from '../../../../../shared/components/edit-text/edit-text';
import { AuthenticationFacade } from '../../authentication.facade.service';
import { validate } from '../../validators/login.validator';
import { Toast } from '../../../../../shared/domain/ui/toast';


@Component({
  selector: 'app-login',
  imports: [RouterLink, EditTextComponent, ToastComponent, ValidationErrors],
  templateUrl: './login.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class Login {

  private readonly authFacade = inject(AuthenticationFacade);
  protected data = signal<LoginRequest>({ email: '', password: '' });
  protected readonly feedback = signal<Toast | null>(null);
  protected readonly validators = validate(this.data);

  constructor(){
    this.authFacade.onToast((toast) => this.feedback.set(toast))
  }

  protected onSubmit() {
    this.authFacade.login(this.data()).subscribe();
  }
}