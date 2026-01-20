import { form, required, email, minLength, pattern } from '@angular/forms/signals';
import { RegisterRequest } from '../domain/authentication/register-request';
import { WritableSignal } from '@angular/core';

export function validate(data: WritableSignal<RegisterRequest>) {
    return form(data, (schemaPath) => {
        required(schemaPath.username, { message: "O nome do usuário é obrigatório" });
        required(schemaPath.email, { message: "O email é obrigatório" });
        email(schemaPath.email, { message: "Formato de email inválido" });
        required(schemaPath.password, { message: "A senha é obrigatória" });
        minLength(schemaPath.password, 8, { message: "A senha precisa ter mais de 8 dígitos" });
        pattern(schemaPath.password, /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/, {
            message: "Requer: maiúsculas, minúsculas, números."
        });
    });
}