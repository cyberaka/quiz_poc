import { Injectable } from '@angular/core';
import { AuthService as Auth0Service } from '@auth0/auth0-angular';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private auth0Service: Auth0Service) {}

  isAuthenticated(): Observable<boolean> {
    return this.auth0Service.isAuthenticated$.pipe(
      map(isAuth => isAuth)
    );
  }
}
