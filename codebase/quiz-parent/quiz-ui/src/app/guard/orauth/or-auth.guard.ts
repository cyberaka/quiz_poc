import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthGuard } from '@auth0/auth0-angular';
import { forkJoin, map, Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrAuthGuard implements CanActivate {
  constructor(
    private guard1: AuthGuard
  ){

  }
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> {
    // Execute both guards, return true if either guard is true
    const checkGuest = localStorage.getItem('mode');
    if(checkGuest == 'guest') {
      return of(true);
    } else {
      return this.guard1.canActivate(route, state).pipe(
        map((result1) => {
          if (result1) {
            return true;
          } else {
            return false;
          }
        })
      );
    }

  }

}
