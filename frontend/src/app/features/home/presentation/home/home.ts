import { Component, inject, ChangeDetectionStrategy, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router'
import { AuthenticationFacade } from '../../../auth/presentation/authentication.facade.service';
@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class Home {
  private authFacade = inject(AuthenticationFacade)
  private route = inject(ActivatedRoute)

  isMenuOpen = false;
  username = signal<string | null>('')
  email = signal<string | null>('')

  constructor() {
    this.username.set(this.route.snapshot.paramMap.get('username'))
    this.email.set(this.route.snapshot.paramMap.get('email'))
  }

  getInitials(name: string | null): string {
    if (!name) return '';
    const parts = name.trim().split(' ');
    if (parts.length > 1) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  logOut() {
    this.authFacade.logout().subscribe();
  }
}