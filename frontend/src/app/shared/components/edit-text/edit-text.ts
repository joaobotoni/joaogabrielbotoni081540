import { Component, Input } from '@angular/core';
import { EditText } from '../../../shared/domain/ui/edit-text';
import { FormField } from '@angular/forms/signals';

@Component({
     selector: 'app-edit-text',
     imports: [FormField],
     templateUrl: "./edit-text.html",
})
export class EditTextComponent {
     @Input({ required: true }) inputProps!: EditText;
}