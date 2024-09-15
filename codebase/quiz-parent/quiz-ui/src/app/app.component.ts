import { Component, OnInit, NgZone  } from '@angular/core';
import { Browser } from '@capacitor/browser';
import { App } from '@capacitor/app';
import { AuthService } from '@auth0/auth0-angular';
import { mergeMap } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { NavigationEnd, Router } from '@angular/router';
import { Capacitor } from '@capacitor/core';
import { DialogService } from './services/dialog.service';
import { HttpService } from './services/http.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit  {
  loginMode: string ='';

  constructor(
    public auth: AuthService,
    private ngZone: NgZone,
    private router: Router,
    private dialog: DialogService,
    private http: HttpService
  ) {
    this.routerListen()
  }

  ngOnInit(): void {
    App.addListener('appUrlOpen', ({ url }) => {
      // Must run inside an NgZone for Angular to pick up the changes
      // https://capacitorjs.com/docs/guides/angular
      this.ngZone.run(() => {
        if (url?.startsWith(environment.auth.authorizationParams.redirect_uri)) {
          if (
            url.includes('state=') &&
            (url.includes('error=') || url.includes('code='))
          ) {
            this.auth
              .handleRedirectCallback(url)
              .pipe(mergeMap(() =>
                Browser.close()
              ))
              .subscribe({
                next: (val) => {
                  console.log('state');
                  console.log(val);
                }
              });
          } else {
            this.auth.isAuthenticated$.subscribe(c => {
              if(!c) {
                this.homeRedirect();
              } else {
                Browser.close().then(c => {
                  console.log(c);
                });
              }
            })

          }
        }
      });
    });

  }

  routerListen() {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        // Check if user is logged in (you can use a service or localStorage)
        const isLoggedIn = localStorage.getItem('mode') === 'guest';
        // Update loginMode based on login status
        this.loginMode = !isLoggedIn ? 'user' : 'guest';
      }
    });
  }

  homeRedirect() {
    this.ngZone.run(() => {
      this.router
      .navigateByUrl('login', { replaceUrl: true }).then(c => {
        Browser.close().then(c => {
          console.log(c);
        }).catch(e => {
          console.log(e);
        });
      });
    });
  }

  logout() {
    const guestMode  = localStorage.getItem('mode')
    if(guestMode && guestMode == 'guest') {
      localStorage.removeItem('mode');
      this.router.navigateByUrl('login', { replaceUrl: true });
    } else {
        let returnTo = environment.auth.authorizationParams.redirect_uri;
        if (Capacitor.isNativePlatform()) {
          this.auth
          .logout({
            logoutParams: {
              returnTo
            },
            async openUrl(url: string) {
            await Browser.open({ url, windowName: '_self' })}
          })
          .subscribe();
        } else {
          this.auth
          .logout({
            logoutParams: {
              returnTo
            },
          }).subscribe((c) => {
          /*  this.router
            .navigateByUrl('login', { replaceUrl: true }); */
          });
        }
    }
  }

  deactivate() {
    this.dialog.showAlert('This action is permanent and irreversible. Are you sure you want to delete your account?',
    [
      {
        text: 'Ok',
        handlerReturn: 'ok'
      },
      {
        text: 'Cancel',
        handlerReturn: 'cancel'
      }
    ]).then(c => {
      if(c == 'ok') {
        this.deleteUser();
      }
    })
  }

  deleteUser() {
    this.http.deleteUser().subscribe(c => {
      if(c) {
        this.logout();
      }
    });
  }
}
