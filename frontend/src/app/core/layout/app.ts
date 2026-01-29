import { Component, inject  } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { ToastService } from '../../shared/services/toast-service';
import { ToastComponent } from '../../shared/components/toast/toast';
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastComponent],
  templateUrl: './app.html',
})
export class App {
  readonly feedback = inject(ToastService);
}
