import { Component, Input } from '@angular/core';
import { InputProps } from '../../domain/ui/input-props';
import { FormField } from '@angular/forms/signals';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-input-component',
  imports: [FormField, CommonModule],
  template: `
    <div class="group w-full">
      <label 
        [for]="inputProps.id" 
        class="mb-1.5 block text-sm font-semibold text-slate-700 transition-colors duration-200 group-focus-within:text-indigo-600">
        {{ inputProps.label }}
      </label>
      
      <input 
        [id]="inputProps.id"
        [type]="inputProps.type"
        [placeholder]="inputProps.placeholder"
        [autocomplete]="inputProps.autocomplete"
        [formField]="inputProps.formField"
        class="block w-full rounded-xl border-0 py-3.5 px-4 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-200 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 bg-slate-50/50 focus:bg-white transition-all duration-200 ease-in-out outline-none sm:text-sm sm:leading-6" />
    </div>
  `
})
export class InputComponent {
  @Input() inputProps: InputProps = {
    id: '',
    label: '',
    type: '',
    placeholder: '',
    autocomplete: '',
    formField: null
  }
}