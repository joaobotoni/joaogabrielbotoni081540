import { Component, Input } from '@angular/core';
import { EditText } from '../../domain/ui/edit-text';
import { FormField } from '@angular/forms/signals';


@Component({
  selector: 'app-edit-text-component',
  imports: [FormField],
  templateUrl: "./edit-text-component.html", 
})
export class EditTextComponent {
  @Input({required: true}) inputProps!: EditText;
}