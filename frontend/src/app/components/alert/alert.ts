import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Message } from '../../domain/ui/message';

@Component({
  selector: 'app-alert',
  imports: [CommonModule],
  template: `
    <div 
      class="mt-2 flex w-full items-start gap-3 rounded-xl p-3.5 shadow-sm ring-1 ring-inset ring-black/5 animate-in fade-in slide-in-from-top-2 duration-300 ease-out"
      [class]="msg.backColor">
      <div class="flex-shrink-0">
        <svg class="h-5 w-5" [class]="msg.iconColor" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" [attr.d]="msg.iconPath" clip-rule="evenodd"/>
        </svg>
      </div>
      <div class="text-sm font-medium leading-5 break-words" [class]="msg.textColor">
        {{ msg.message }}
      </div>
    </div>
  `
})
export class Alert {
  @Input() msg: Message = {
    message: '',
    backColor: '',
    textColor: '',
    iconColor: '',
    iconPath: ''
  }
}