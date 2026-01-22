import { form, required, email } from '@angular/forms/signals';
import { WritableSignal } from '@angular/core';
import { LoginRequest } from '../domain/login.request';

export function validate(data: WritableSignal<LoginRequest>) {
    return form(data, (schemaPath) => {
        required(schemaPath.email, { message: "O email é obrigatório" });
        email(schemaPath.email, { message: "Formato de email inválido" });
        required(schemaPath.password, { message: "A senha é obrigatória" });
    });
}