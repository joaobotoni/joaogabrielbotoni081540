import { RouterLink } from "@angular/router";
import { Component, inject, ChangeDetectionStrategy, signal } from '@angular/core';
import { RegisterRequest } from '../../domain/register.request';
import { EditTextComponent } from '../../../../../shared/components/edit-text/edit-text';
import ValidationErrors from '../../../../../shared/components/validation-errors/validation-errors';
import { ToastComponent } from '../../../../../shared/components/toast/toast';
import { validate } from '../../validators/register.validator';
import { AuthenticationFacade } from '../../authentication.facade.service';
import { Toast } from "../../../../../shared/domain/ui/toast";


@Component({
  selector: 'app-register',
  imports: [RouterLink, EditTextComponent, ValidationErrors, ToastComponent],
  templateUrl: './register.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export default class Register {


  private readonly authFacade = inject(AuthenticationFacade);

  protected readonly data = signal<RegisterRequest>({ username: '', email: '', password: '', });
  protected readonly feedback = signal<Toast | null>(null);
  protected readonly validators = validate(this.data);

  constructor() {
    this.authFacade.onToast(toast => this.feedback.set(toast));
  }

  protected onSubmit() {
    this.authFacade.register(this.data()).subscribe();
  }
}