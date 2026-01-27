import { Component, inject, ChangeDetectionStrategy, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router'
import { AuthenticationFacade } from '../../../auth/services/authentication.facade.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { AuthenticationResponse } from '../../../auth/presentation/domain/authentication.response';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class Home {
  private authFacade = inject(AuthenticationFacade)
  private route = inject(ActivatedRoute)
  private data = toSignal(this.route.data, {requireSync : true})

  public user = computed(() => this.data()['user'] as AuthenticationResponse)
  public isMenuOpen = false;

  public getInitials(name: string | null): string {
    if (!name) return '';
    const parts = name.trim().split(' ');
    if (parts.length > 1) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  public logOut() {
    this.authFacade.logout().subscribe();
  }
}