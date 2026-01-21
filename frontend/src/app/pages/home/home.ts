import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router'
import { Authentication } from '../../services/authentication/authentication';
@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
})
export default class Home {
  private auth = inject(Authentication)
  private router = inject(Router)
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
    this.auth.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: () => {
        this.router.navigate(['/login']);
      }
    })
  }
}