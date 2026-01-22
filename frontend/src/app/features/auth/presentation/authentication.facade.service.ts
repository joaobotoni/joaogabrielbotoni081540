import { inject, Injectable, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap, catchError, throwError, map } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthenticationApiService } from '../services/authentication.api.service';
import { LoginRequest } from './domain/login.request';
import { AuthenticationResponse } from './domain/authentication.response';
import { RegisterRequest } from './domain/register.request';
import { Error, Success, Toast } from '../../../shared/domain/ui/toast';

@Injectable({ providedIn: 'root' })
export class AuthenticationFacade {
 
}